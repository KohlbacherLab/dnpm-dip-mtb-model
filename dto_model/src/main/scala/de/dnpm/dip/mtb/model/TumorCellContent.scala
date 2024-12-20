package de.dnpm.dip.mtb.model


import de.dnpm.dip.model.{
  Id,
  Reference,
  Observation,
  Patient
}
import de.dnpm.dip.coding.{
  Coding,
  CodedEnum,
  DefaultCodeSystem
}
import play.api.libs.json.{
  Json,
  OFormat
}



final case class TumorCellContent
(
  id: Id[TumorCellContent],
  patient: Reference[Patient],
  specimen: Reference[TumorSpecimen],
  method: Coding[TumorCellContent.Method.Value],
  value: Double,
)
extends Observation[Double]


object TumorCellContent
{

  object Method
  extends CodedEnum("dnpm-dip/mtb/tumor-cell-content/method")
  with DefaultCodeSystem
  {
    val Histologic    = Value("histologic")
    val Bioinformatic = Value("bioinformatic")
  
    override val display =
      Map(
        Histologic    -> "Histologisch",
        Bioinformatic -> "Bioinformatisch"
      )
  }


  implicit val format: OFormat[TumorCellContent] =
    Json.format[TumorCellContent]
}
