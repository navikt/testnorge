import { FormikProps } from 'formik'
import _get from 'lodash/get'
import _last from 'lodash/last'
import { FormikPartner, Partner, PersonHentet, Relasjon, SivilstandObj } from './partnerTypes'

export default function Partnerliste(
	formikBag: FormikProps<{}>,
	personFoerLeggTil: PersonHentet,
	path: string
) {
	const tidligerePartnere: Array<FormikPartner> = mapTidligerePartnere(personFoerLeggTil)
	const sisteTidligerePartner: FormikPartner = _last(tidligerePartnere)

	const formikPartnere: Array<FormikPartner> = mapNyePartnere(
		formikBag,
		path,
		sisteTidligerePartner
	)
	const oppdatertPartner: boolean = formikPartnere.some((partner: FormikPartner) => !partner.ny)
	const tidligerePartnereUtenOppdateringer: Array<FormikPartner> = oppdatertPartner
		? tidligerePartnere.slice(0, -1)
		: tidligerePartnere

	const partnere: Array<FormikPartner> =
		tidligerePartnere.length > 0
			? [...tidligerePartnereUtenOppdateringer, ...formikPartnere]
			: formikPartnere

	return {
		partnere,
		partnereUtenomFormikBag: tidligerePartnereUtenOppdateringer.length,
		oppdatertPartner
	}
}

const finnTidligerePartnere = (personFoerLeggTil: PersonHentet) =>
	_get(personFoerLeggTil, 'tpsf.relasjoner', [])
		.filter((relasjon: Relasjon) => relasjon.relasjonTypeNavn === 'PARTNER')
		.map((relasjon: Relasjon) => relasjon.personRelasjonMed)

const mapTidligerePartnere = (personFoerLeggTil: PersonHentet) => {
	const tidligerePartnerliste: Array<Partner> = finnTidligerePartnere(personFoerLeggTil).reverse()
	return (
		tidligerePartnerliste.map((partner: Partner) => ({
			ny: false,
			data: {
				...partner,
				sivilstander: partner.sivilstander
					.map((siv: SivilstandObj) => {
						return { ny: false, data: siv }
					})
					.reverse()
			}
		})) || []
	)
}

const mapNyePartnere = (
	formikBag: FormikProps<{}>,
	path: string,
	sisteTidligerePartner: FormikPartner
) =>
	_get(formikBag.values, path, []).map((partner: Partner) =>
		partner.hasOwnProperty('identtype')
			? {
					ny: true,
					data: {
						...partner,
						sivilstander: partner.sivilstander.map((siv: SivilstandObj) => {
							return { ny: true, data: siv }
						})
					}
			  }
			: {
					...sisteTidligerePartner,
					data: {
						...sisteTidligerePartner.data,
						sivilstander: [
							...sisteTidligerePartner.data.sivilstander,
							...partner.sivilstander.map((siv: SivilstandObj) => {
								return { ny: true, data: siv }
							})
						]
					}
			  }
	)
