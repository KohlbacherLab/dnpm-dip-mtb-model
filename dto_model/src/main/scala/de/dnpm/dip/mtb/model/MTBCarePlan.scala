package de.dnpm.dip.mtb.model


import java.time.LocalDate
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem,
  CodedEnum,
  DefaultCodeSystem
}
import de.dnpm.dip.model.{
  Id,
  Period,
  Reference,
  Patient,
  CarePlan
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class MTBCarePlan
(
  id: Id[MTBCarePlan],
  patient: Reference[Patient],
  indication: Reference[MTBDiagnosis],
  issuedOn: LocalDate,
  statusReason: Option[Coding[MTBCarePlan.StatusReason.Value]],
  protocol: Option[String],
  medicationRecommendations: Option[List[MTBMedicationRecommendation]],
  geneticCounselingRecommendation: Option[GeneticCounselingRecommendation],
  studyEnrollmentRecommendations: Option[List[StudyEnrollmentRecommendation]]
)
extends CarePlan


object MTBCarePlan
{

  object StatusReason
  extends CodedEnum("dnpm-dip/mtb/careplan/status-reason")
  with DefaultCodeSystem
  {
    val NoTarget = Value("no-target")

    override val display =
      Map(
        NoTarget -> "Keine Therapeutische Konsequenz"
      )
  }

  implicit val format: OFormat[MTBCarePlan] =
    Json.format[MTBCarePlan]

}
