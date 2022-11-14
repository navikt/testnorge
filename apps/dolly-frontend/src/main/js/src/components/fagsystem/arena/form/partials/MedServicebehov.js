import React, { useContext } from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import _get from 'lodash/get'
import { Alert } from '@navikt/ds-react'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

const errorPaths = [
	`arenaforvalter.aap115[0].fraDato`,
	`arenaforvalter.aap[0].tilDato`,
	`arenaforvalter.dagpenger[0].tilDato`,
]

const feilmelding25 = 'Vedtak kan ikke overlappe med 25-årsdagen til person. '
const feilmelding67 = 'Person kan ikke ha vedtak etter fylte 67 år. '
const feilmeldingFiks =
	'Vennligst endre vedtaksperioden eller sett en spesifikk alder/fødsel på person for å unngå denne feilen.'

const getFeilmelding = (formikBag) => {
	let melding = ''
	let har25Feil = false
	let har67Feil = false
	for (let path of errorPaths) {
		const feil = _get(formikBag.errors, path)
		if (feil && !har25Feil && feil.includes('25')) {
			har25Feil = true
			melding += feilmelding25
		} else if (feil && !har67Feil && feil.includes('67')) {
			har67Feil = true
			melding += feilmelding67
		}
	}
	return melding ? melding + feilmeldingFiks : null
}

export const MedServicebehov = ({ formikBag, path }) => {
	const opts = useContext(BestillingsveilederContext)
	const { arenaforvalter } = formikBag.values
	const feilmelding = getFeilmelding(formikBag)

	return (
		<React.Fragment>
			{!opts.is.leggTil && !opts.is.importTestnorge && feilmelding && (
				<Alert variant={'warning'} style={{ marginBottom: '20px' }}>
					{feilmelding}
				</Alert>
			)}
			<FormikSelect
				name={`${path}.kvalifiseringsgruppe`}
				label="Servicebehov"
				options={Options('kvalifiseringsgruppe')}
				size="large"
			/>
			{arenaforvalter.aap115 && (
				<Kategori title="11-5-vedtak">
					<FormikDatepicker name={`${path}.aap115[0].fraDato`} label="Fra dato" />
				</Kategori>
			)}

			{arenaforvalter.aap && (
				<Kategori title="AAP-vedtak UA - positivt utfall">
					<FormikDatepicker name={`${path}.aap[0].fraDato`} label="Fra dato" />
					<FormikDatepicker name={`${path}.aap[0].tilDato`} label="Til dato" />
				</Kategori>
			)}

			{arenaforvalter.dagpenger && (
				<Kategori
					hjelpetekst={'Foreløpig er kun ordinære dagpenger støttet'}
					title="Dagpengevedtak"
				>
					<FormikSelect
						name={`${path}.dagpenger[0].rettighetKode`}
						options={Options('rettighetKode')}
						isDisabled={true}
						value={'DAGO'} // Endre disabled og denne når flere koder blir støttet
						label="Rettighetskode"
						size={'xlarge'}
					/>
					<FormikDatepicker name={`${path}.dagpenger[0].fraDato`} label="Fra dato" />
					<FormikDatepicker name={`${path}.dagpenger[0].tilDato`} label="Til dato" />
					<FormikDatepicker name={`${path}.dagpenger[0].mottattDato`} label="Mottatt dato" />
				</Kategori>
			)}
		</React.Fragment>
	)
}
