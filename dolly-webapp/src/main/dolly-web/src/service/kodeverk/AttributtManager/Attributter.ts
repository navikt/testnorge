import { Attributt, KategoriTypes, InputType, DataSource } from './Types'

import TpsfPersoninformasjon from './tpsf/Personinformasjon'
import TpsfAdresse from './tpsf/Adresse'
import TpsfRelasjon from './tpsf/Relasjon'
import Krr from './krr/Krr'
import SigrunInntekt from './sigrun/Inntekt'

const AttributtListe: Attributt[] = [
	...TpsfPersoninformasjon,
	...TpsfAdresse,
	...Krr,
	...TpsfRelasjon,
	...SigrunInntekt
]

export default AttributtListe
