import { Attributt, KategoriTypes, InputType, DataSource } from './Types'

import TpsfPersoninformasjon from './tpsf/Personinformasjon'
import TpsfAdresse from './tpsf/Adresse'
import TpsfRelasjon from './tpsf/Relasjon'
import Krr from './krr/Krr'
import Arbeidsforhold from './aareg/Arbeidsforhold'
import SigrunInntekt from './sigrun/Inntekt'
import Doedsbo from './pdlf/Doedsbo'
import UtenlandsId from './pdlf/UtenlandsId'
import Arena from './arena/Arena'
import Inst2 from './inst2/Inst2'

const AttributtListe: Attributt[] = [
	...TpsfPersoninformasjon,
	...TpsfAdresse,
	...TpsfRelasjon,
	...Arbeidsforhold,
	...SigrunInntekt,
	...Krr,
	...Doedsbo,
	...UtenlandsId,
	...Arena,
	...Inst2
]

export default AttributtListe
