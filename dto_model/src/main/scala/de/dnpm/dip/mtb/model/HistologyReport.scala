package de.dnpm.dip.mtb.model


import java.time.LocalDate
import de.dnpm.dip.coding.Coding
import de.dnpm.dip.coding.icd.ICDO3
import de.dnpm.dip.model.{
  Id,
  Patient,
  Reference,
  Observation
}
import play.api.libs.json.{
  Json,
  OFormat
}


final case class TumorMorphology
(
  id: Id[TumorMorphology],
  patient: Reference[Patient],
  specimen: Reference[TumorSpecimen],
  value: Coding[ICDO3.M],
  notes: Option[String]
)
extends Observation[Coding[ICDO3.M]]

object TumorMorphology
{
  implicit val format: OFormat[TumorMorphology] =
    Json.format[TumorMorphology]
}


final case class HistologyReport
(
  id: Id[HistologyReport],
  patient: Reference[Patient],
  specimen: Reference[TumorSpecimen],
  issuedOn: LocalDate,
  results: HistologyReport.Results
)

object HistologyReport
{

  final case class Results
  (
    tumorMorphology: Option[TumorMorphology],
    tumorCellContent: Option[TumorCellContent]
  )

  implicit val formatResults: OFormat[Results] =
    Json.format[Results]

  implicit val format: OFormat[HistologyReport] =
    Json.format[HistologyReport]
}
