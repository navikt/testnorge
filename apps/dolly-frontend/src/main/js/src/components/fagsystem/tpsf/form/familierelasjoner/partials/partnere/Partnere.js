import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import _has from 'lodash/has'
import {
	DollyFaBlokk,
	DollyFieldArrayWrapper,
	FieldArrayAddButton
} from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import { erOpprettNyPartnerGyldig } from './sivilstand/SivilstandOptions'
import Partnerliste from './partnere/partnerliste'
import PartnerForm from './partnere/partnerForm'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	sivilstander: [{ sivilstand: '', sivilstandRegdato: '' }],
	harFellesAdresse: false,
	alder: Formatters.randomIntInRange(30, 60),
	doedsdato: null,
	spesreg: '',
	utenFastBopel: false,
	statsborgerskap: '',
	statsborgerskapRegdato: '',
	statsborgerskapTildato: ''
}

const ugyldigSivilstandState = errors =>
	_get(errors, 'tpsf.relasjoner.partnere', []).some(partner => _has(partner, 'sivilstander'))

const sistePartner = (partnere = []) => partnere[partnere.length - 1]

const sisteSivilstand = (partner = {}) => {
	const sivilstander = partner.sivilstander || []
	return sivilstander[sivilstander.length - 1]
}

const sisteTidligereSivilstandRegdato = partnere => {
	const tidligerePartnere = partnere.filter(partner => !partner.ny)
	if (tidligerePartnere.length < 1) return null

	const tidligereSivilstander = sistePartner(tidligerePartnere).sivilstander.filter(
		sivilstand => !sivilstand.ny
	)
	return tidligereSivilstander[tidligereSivilstander.length - 1].sivilstandRegdato
}

// Det er 3 kriterier for å opprette ny partner
// - Må ha regDato for forholder (error validering sjekker om dato er gyldig)
// - Må ha sivilstandKode som er gyldig som "siste forholdstype"
// - Må ikke finnes errors i sivilstandform
export const sjekkKanOppretteNyPartner = (partnere, formikBag) => {
	if (partnere.length < 1) return null
	const { sivilstand, sivilstandRegdato } = sisteSivilstand(sistePartner(partnere))

	const gyldigKode = erOpprettNyPartnerGyldig(sivilstand)

	const harRegdato = sivilstandRegdato !== null
	const harGyldigSivilstander = !ugyldigSivilstandState(formikBag.errors)
	return harGyldigSivilstander && gyldigKode && harRegdato
}

const path = 'tpsf.relasjoner.partnere'
export const Partnere = ({ formikBag, personFoerLeggTil }) => (
	<FieldArray name={path}>
		{arrayHelpers => {
			// Vil også vise partnere og sivilstander fra tidligere bestillinger uten å legge dem i formikBag.
			// Lager derfor nytt partnerarray med både nye og tidligere partnere tagget med 'ny: true/false'.
			const { partnere, partnereUtenomFormikBag, oppdatertPartner } = Partnerliste(
				formikBag,
				personFoerLeggTil,
				path
			)
			const kanOppretteNyPartner = sjekkKanOppretteNyPartner(partnere, formikBag)
			const addNewEntry = () => arrayHelpers.push(initialValues)

			return (
				<ErrorBoundary>
					<DollyFieldArrayWrapper header="Partner">
						{partnere.map((c, idx) => {
							const formikIdx =
								!c.ny && !oppdatertPartner
									? idx - (partnereUtenomFormikBag - 1)
									: idx - partnereUtenomFormikBag

							const formikPath = `${path}[${formikIdx}]`
							const isLast = idx === partnere.length - 1

							// Det er kun mulig å slette siste forhold
							const showRemove = isLast && idx > 0 && c.ny
							const clickRemove = () => arrayHelpers.remove(formikIdx)

							return (
								<DollyFaBlokk
									key={idx}
									idx={idx}
									header="Partner"
									handleRemove={clickRemove}
									showDeleteButton={showRemove}
								>
									<PartnerForm
										path={formikPath}
										formikBag={formikBag}
										partner={c}
										locked={idx !== partnere.length - 1}
										minDatoSivilstand={sisteTidligereSivilstandRegdato(partnere)}
									/>
								</DollyFaBlokk>
							)
						})}
						<FieldArrayAddButton
							hoverText={
								!kanOppretteNyPartner
									? 'Forhold med tidligere partner må avsluttes (skilt eller enke/-mann)'
									: false
							}
							addEntryButtonText="Legg til ny partner"
							onClick={addNewEntry}
							disabled={!kanOppretteNyPartner}
						/>
					</DollyFieldArrayWrapper>
				</ErrorBoundary>
			)
		}}
	</FieldArray>
)
