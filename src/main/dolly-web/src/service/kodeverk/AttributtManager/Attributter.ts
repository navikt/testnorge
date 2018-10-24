import { Attributt, KategoriTypes, InputType, DataSource } from './Types'
import * as yup from 'yup'

import TpsfPersoninformasjon from './tpsf/Personinformasjon'
import TpsfAdresse from './tpsf/Adresse'
import TpsfRelasjon from './tpsf/Relasjon'
import SigrunInntekt from './sigrun/Inntekt'

const AttributtListe: Attributt[] = [
	...TpsfPersoninformasjon,
	...TpsfAdresse,
	...TpsfRelasjon,
	...SigrunInntekt
]

export default AttributtListe
