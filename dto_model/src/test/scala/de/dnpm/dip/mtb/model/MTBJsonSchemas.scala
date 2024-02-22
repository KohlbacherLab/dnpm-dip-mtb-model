package de.dnpm.dip.mtb.model


import java.time.LocalDate
import java.time.temporal.Temporal
import cats.data.NonEmptyList
import play.api.libs.json.JsObject
import json.{
  Json,
  Schema
}
import com.github.andyglow.json.Value
import com.github.andyglow.jsonschema.AsPlay._
import com.github.andyglow.jsonschema.CatsSupport._
import json.schema.Version._
import de.dnpm.dip.coding.Coding
import de.dnpm.dip.model.{
  Patient,
  Period,
  OpenEndPeriod
}


trait MTBJsonSchemas
{

  import Schema.`object`.Field

  implicit def codingSchema[T](
    implicit
    cl: Class[T],
    strSchema: Schema[String],
    sys: Coding.System[T],
  ): Schema[Coding[T]] =
    Schema.`object`(
      Field[String]("code",Schema[String],true),
      Field[String]("display",Schema[String],false),
      Field[String]("system",Schema[String],false),
      Field[String]("version",Schema[String],false)
    )
    .toDefinition(s"Coding[${cl.getSimpleName}]")


  implicit def periodSchema[T <: Temporal: Schema](
    implicit cl: Class[T]
  ): Schema[Period[T]] =
    Json.schema[OpenEndPeriod[T]]
      .asInstanceOf[Schema[Period[T]]]
      .toDefinition(s"Period[${cl.getSimpleName}]")
/*
  implicit val datePeriodSchema: Schema[Period[LocalDate]] =
    Json.schema[OpenEndPeriod[LocalDate]]
      .asInstanceOf[Schema[Period[LocalDate]]]
      .toDefinition("Period[Date]")
*/

  implicit val consentSchema: Schema[JsObject] = 
    Schema.`object`.Free[JsObject]()


  implicit val patientSchema: Schema[Patient] =
    Json.schema[Patient]


  implicit val diagnosisSchema: Schema[MTBDiagnosis] =
    Json.schema[MTBDiagnosis]

  implicit val episodeSchema: Schema[MTBEpisode] =
    Json.schema[MTBEpisode]


  implicit val medicationTherapySchema: Schema[MTBMedicationTherapy] =
    Json.schema[MTBMedicationTherapy]


  implicit val oncoProcedureSchema: Schema[OncoProcedure] =
    Json.schema[OncoProcedure]


  implicit val performanceStatusSchema: Schema[PerformanceStatus] =
    Json.schema[PerformanceStatus]


  implicit val tumorSpecimenSchema: Schema[TumorSpecimen] =
    Json.schema[TumorSpecimen]


  implicit val histologyReportSchema: Schema[HistologyReport] =
    Json.schema[HistologyReport]


  implicit val ihcReportSchema: Schema[IHCReport] =
    Json.schema[IHCReport]


  implicit val ngsReportSchema: Schema[NGSReport] =
    Json.schema[NGSReport]


  implicit val carePlanSchema: Schema[MTBCarePlan] =
    Json.schema[MTBCarePlan]


  implicit val claimSchema: Schema[Claim] =
    Json.schema[Claim]


  implicit val claimResponseSchema: Schema[ClaimResponse] =
    Json.schema[ClaimResponse]


  implicit val responseSchema: Schema[Response] =
    Json.schema[Response]


/*    
  implicit val _Schema: Schema[] =
    Json.schema[]


*/

  implicit val episodesSchema: Schema[NonEmptyList[MTBEpisode]] =
    Json.schema[NonEmptyList[MTBEpisode]]

  implicit val schema: Schema[MTBPatientRecord] =
    Json.schema[MTBPatientRecord]

}

object MTBJsonSchemas extends MTBJsonSchemas