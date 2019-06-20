import { Attributt, KategoriTypes, InputType, DataSource } from './Types'

import TpsfPersoninformasjon from './tpsf/Personinformasjon'
import TpsfAdresse from './tpsf/Adresse'
import TpsfRelasjon from './tpsf/Relasjon'
import Krr from './krr/Krr'
import Arbeidsforhold from './aareg/Arbeidsforhold'
import SigrunInntekt from './sigrun/Inntekt'
import Doedsbo from './pdlf/Doedsbo'
import Arena from './arena/Arena'

const AttributtListe: Attributt[] = [
	...TpsfPersoninformasjon,
	...TpsfAdresse,
	...TpsfRelasjon,
	...Arbeidsforhold,
	...SigrunInntekt,
	...Krr,
	...Doedsbo,
	...Arena
]

export default AttributtListe
