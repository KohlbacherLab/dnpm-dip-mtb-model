package de.dnpm.dip.mtb.model


import java.time.LocalDate
import de.dnpm.dip.coding.{
  Coding,
  CodeSystem
}
import de.dnpm.dip.model.{
  Id,
  Period,
  Reference,
  Patient,
  Episode,
  TransferTAN
}
import play.api.libs.json.{
  Json, 
  OFormat
}


final case class MTBEpisode
(
  id: Id[MTBEpisode],
  transferTan: Option[Id[TransferTAN]],
  patient: Reference[Patient],
  period: Period[LocalDate],
  status: Coding[Episode.Status.Value],
  diagnoses: List[Reference[MTBDiagnosis]]
)
extends Episode


object MTBEpisode
{
  implicit val format: OFormat[MTBEpisode] =
    Json.format[MTBEpisode]
}
