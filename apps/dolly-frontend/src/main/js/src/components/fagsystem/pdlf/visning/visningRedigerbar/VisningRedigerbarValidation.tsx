import * as Yup from 'yup'
import { ifPresent } from '@/utils/YupValidations'
import { doedsfall, navn } from '@/components/fagsystem/pdlf/form/validation/validation'
import {
	adressebeskyttelse,
	bostedsadresse,
	doedfoedtBarn,
	forelderBarnRelasjon,
	innflytting,
	kontaktadresse,
	kontaktDoedsbo,
	oppholdsadresse,
	sivilstand,
	statsborgerskap,
	utenlandskId,
	utflytting,
	vergemaal,
} from '@/components/fagsystem/pdlf/form/validation/partials'
import {
	deltBosted,
	foreldreansvarForBarn,
} from '@/components/fagsystem/pdlf/form/validation/partials/familierelasjoner'

export const visningRedigerbarValidation = Yup.object().shape(
	{
		navn: ifPresent('navn', navn),
		doedsfall: ifPresent('doedsfall', doedsfall),
		statsborgerskap: ifPresent('statsborgerskap', statsborgerskap),
		innflytting: ifPresent('innflytting', innflytting),
		utflytting: ifPresent('utflytting', utflytting),
		vergemaal: ifPresent('vergemaal', vergemaal),
		bostedsadresse: ifPresent('bostedsadresse', bostedsadresse),
		oppholdsadresse: ifPresent('oppholdsadresse', oppholdsadresse),
		kontaktadresse: ifPresent('kontaktadresse', kontaktadresse),
		adressebeskyttelse: ifPresent('adressebeskyttelse', adressebeskyttelse),
		deltBosted: ifPresent('deltBosted', deltBosted),
		sivilstand: ifPresent('sivilstand', sivilstand),
		kontaktinformasjonForDoedsbo: ifPresent('kontaktinformasjonForDoedsbo', kontaktDoedsbo),
		forelderBarnRelasjon: ifPresent('forelderBarnRelasjon', forelderBarnRelasjon),
		foreldreansvar: ifPresent(
			'foreldreansvar',
			Yup.mixed().when('foreldreansvar', {
				is: (foreldreansvar) => Array.isArray(foreldreansvar),
				then: () => Yup.array().of(foreldreansvarForBarn),
				otherwise: () => foreldreansvarForBarn,
			}),
		),
		doedfoedtBarn: ifPresent('doedfoedtBarn', doedfoedtBarn),
		utenlandskIdentifikasjonsnummer: ifPresent('utenlandskIdentifikasjonsnummer', utenlandskId),
	},
	[
		['navn', 'navn'],
		['doedsfall', 'doedsfall'],
		['statsborgerskap', 'statsborgerskap'],
		['innflytting', 'innflytting'],
		['utflytting', 'utflytting'],
		['vergemaal', 'vergemaal'],
		['bostedsadresse', 'bostedsadresse'],
		['oppholdsadresse', 'oppholdsadresse'],
		['kontaktadresse', 'kontaktadresse'],
		['adressebeskyttelse', 'adressebeskyttelse'],
		['deltBosted', 'deltBosted'],
		['sivilstand', 'sivilstand'],
		['kontaktinformasjonForDoedsbo', 'kontaktinformasjonForDoedsbo'],
		['forelderBarnRelasjon', 'forelderBarnRelasjon'],
		['foreldreansvar', 'foreldreansvar'],
		['doedfoedtBarn', 'doedfoedtBarn'],
		['utenlandskIdentifikasjonsnummer', 'utenlandskIdentifikasjonsnummer'],
	],
)
