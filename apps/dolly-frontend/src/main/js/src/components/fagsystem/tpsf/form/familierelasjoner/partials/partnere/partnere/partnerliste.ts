import { FormikProps } from 'formik'
import _get from 'lodash/get'
import { Partner, PersonHentet, Relasjon, Relasjonstyper, Sivilstand } from './partnerTypes'

export default function Partnerliste(
	formikBag: FormikProps<{}>,
	personFoerLeggTil: PersonHentet,
	path: string
) {
	const tidligerePartnere: Array<Partner> = mapTidligerePartnere(personFoerLeggTil)
	const sisteTidligerePartner: Partner = tidligerePartnere[tidligerePartnere.length - 1]
	const formikPartnere: Array<Partner> = mapNyePartnere(formikBag, path, sisteTidligerePartner)
	const oppdatertPartner: boolean = formikPartnere.some((partner: Partner) => !partner.ny)

	oppdatertPartner && tidligerePartnere.pop() //Fjerner tidligere partner som er oppdatert

	const partnere: Array<Partner> = tidligerePartnere.concat(formikPartnere)

	return {
		partnere,
		partnereUtenomFormikBag: tidligerePartnere.length,
		oppdatertPartner,
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
		sivilstander: mapSivilstand(partner.sivilstander, false).reverse(),
	}))
}

const mapNyePartnere = (formikBag: FormikProps<{}>, path: string, sisteTidligerePartner: Partner) =>
	_get(formikBag.values, path, []).map((partner: Partner) =>
		partner.hasOwnProperty('identtype')
			? {
					...partner,
					ny: true,
					sivilstander: mapSivilstand(partner.sivilstander, true),
			  }
			: {
					...sisteTidligerePartner,
					sivilstander: sisteTidligerePartner.sivilstander.concat(
						mapSivilstand(partner.sivilstander, true)
					),
			  }
	)
