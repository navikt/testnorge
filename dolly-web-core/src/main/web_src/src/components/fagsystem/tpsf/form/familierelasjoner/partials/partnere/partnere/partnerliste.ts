import { FormikProps } from 'formik'
import _get from 'lodash/get'
import _last from 'lodash/last'
import { Partner, PersonHentet, Relasjon, Sivilstand, Relasjonstyper } from './partnerTypes'

export default function Partnerliste(
	formikBag: FormikProps<{}>,
	personFoerLeggTil: PersonHentet,
	path: string
) {
	const tidligerePartnere: Array<Partner> = mapTidligerePartnere(personFoerLeggTil)
	const sisteTidligerePartner: Partner = tidligerePartnere[tidligerePartnere.length - 1]

	const formikPartnere: Array<Partner> = mapNyePartnere(formikBag, path, sisteTidligerePartner)
	const oppdatertPartner: boolean = formikPartnere.some((partner: Partner) => !partner.ny)
	const tidligerePartnereUtenOppdateringer: Array<Partner> = oppdatertPartner
		? tidligerePartnere.slice(0, -1)
		: tidligerePartnere

	const partnere: Array<Partner> = (tidligerePartnere.length > 0
		? tidligerePartnereUtenOppdateringer
		: []
	).concat(formikPartnere)

	return {
		partnere,
		partnereUtenomFormikBag: tidligerePartnereUtenOppdateringer.length,
		oppdatertPartner
	}
}

const finnTidligerePartnere = (personFoerLeggTil: PersonHentet): Array<Partner> =>
	_get(personFoerLeggTil, 'tpsf.relasjoner', [])
		.filter((relasjon: Relasjon) => relasjon.relasjonTypeNavn === Relasjonstyper.Partner)
		.map((relasjon: Relasjon) => relasjon.personRelasjonMed)

const mapSivilstand = (sivilstander: Array<Sivilstand>, ny: boolean): Array<Sivilstand> =>
	sivilstander.map((sivilstand: Sivilstand) => ({ ...sivilstand, ny: ny }))

const mapTidligerePartnere = (personFoerLeggTil: PersonHentet): Array<Partner> => {
	const tidligerePartnerliste: Array<Partner> = finnTidligerePartnere(personFoerLeggTil).reverse()
	return tidligerePartnerliste.map((partner: Partner) => ({
		...partner,
		ny: false,
		sivilstander: mapSivilstand(partner.sivilstander, false).reverse()
	}))
}

const mapNyePartnere = (formikBag: FormikProps<{}>, path: string, sisteTidligerePartner: Partner) =>
	_get(formikBag.values, path, []).map((partner: Partner) =>
		partner.hasOwnProperty('identtype')
			? {
					...partner,
					ny: true,
					sivilstander: mapSivilstand(partner.sivilstander, true)
			  }
			: {
					...sisteTidligerePartner,
					sivilstander: sisteTidligerePartner.sivilstander.concat(
						mapSivilstand(partner.sivilstander, true)
					)
			  }
	)
