import { Attributt, KategoriTypes, InputType, DataSource } from './Types'
import * as yup from 'yup'

import TpsfPersoninformasjon from './tpsf/Personinformasjon'
import TpsfAdresse from './tpsf/Adresse'
import TpsfRelasjon from './tpsf/Relasjon'

const AttributtListe: Attributt[] = [...TpsfPersoninformasjon, ...TpsfAdresse, ...TpsfRelasjon]

export default AttributtListe
