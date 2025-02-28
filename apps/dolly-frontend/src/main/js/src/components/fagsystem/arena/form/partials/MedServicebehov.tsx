import React, { useContext } from 'react'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as _ from 'lodash-es'
import { Alert } from '@navikt/ds-react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

const errorPaths = [
	`arenaforvalter.aap115[0].fraDato`,
	`arenaforvalter.aap[0].tilDato`,
	`arenaforvalter.dagpenger[0].tilDato`,
]

const feilmelding25 = 'Vedtak kan ikke overlappe med 25-årsdagen til person. '
const feilmelding67 = 'Person kan ikke ha vedtak etter fylte 67 år. '
const feilmeldingFiks =
	'Vennligst endre vedtaksperioden eller sett en spesifikk alder/fødsel på person for å unngå denne feilen.'

const getFeilmelding = (formMethods) => {
	let melding = ''
	let har25Feil = false
	let har67Feil = false
	for (let path of errorPaths) {
		const feil = _.get(formMethods.formState.errors, path)?.message
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

export const MedServicebehov = ({ formMethods, path }) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { arenaforvalter } = formMethods.getValues()
	const feilmelding = getFeilmelding(formMethods)

	return (
		<React.Fragment>
			{!opts.is.leggTil && !opts.is.importTestnorge && feilmelding && (
				<Alert variant={'warning'} style={{ marginBottom: '20px' }}>
					{feilmelding}
				</Alert>
			)}
			<div className="flexbox--flex-wrap">
				<FormSelect
					name={`${path}.kvalifiseringsgruppe`}
					label="Servicebehov"
					options={Options('kvalifiseringsgruppe')}
					size="xlarge"
				/>
				{!opts.personFoerLeggTil?.arenaforvalteren && (
					<FormDatepicker
						name={`${path}.aktiveringDato`}
						label="Aktiveringsdato"
						minDate={new Date('2002-12-30')}
					/>
				)}
			</div>
			{arenaforvalter.aap115 && (
				<Kategori title="11-5-vedtak">
					<FormDatepicker name={`${path}.aap115[0].fraDato`} label="Fra dato" />
				</Kategori>
			)}

			{arenaforvalter.aap && (
				<Kategori title="AAP-vedtak UA - positivt utfall">
					<div className="flexbox--flex-wrap">
						<FormDatepicker name={`${path}.aap[0].fraDato`} label="Fra dato" />
						<FormDatepicker name={`${path}.aap[0].tilDato`} label="Til dato" />
					</div>
				</Kategori>
			)}

			{arenaforvalter.dagpenger && (
				<Kategori
					hjelpetekst={'Foreløpig er kun ordinære dagpenger støttet'}
					title="Dagpengevedtak"
				>
					<div className="flexbox--flex-wrap">
						<FormSelect
							name={`${path}.dagpenger[0].rettighetKode`}
							options={Options('rettighetKode')}
							isDisabled={true}
							value={'DAGO'} // Endre disabled og denne når flere koder blir støttet
							label="Rettighetskode"
							size={'xlarge'}
						/>
						<FormDatepicker name={`${path}.dagpenger[0].fraDato`} label="Fra dato" />
						<FormDatepicker name={`${path}.dagpenger[0].tilDato`} label="Til dato" />
						<FormDatepicker name={`${path}.dagpenger[0].mottattDato`} label="Mottatt dato" />
					</div>
				</Kategori>
			)}
		</React.Fragment>
	)
}
