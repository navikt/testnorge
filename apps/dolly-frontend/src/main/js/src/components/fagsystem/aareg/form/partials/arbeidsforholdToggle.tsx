import React, { ReactElement, useState } from 'react'
import _get from 'lodash/get'
import styled from 'styled-components'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { AmeldingForm } from './ameldingForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAaregOrg,
	initialAaregPers,
	initialArbeidsforholdOrg,
	initialArbeidsforholdPers,
	initialValues,
} from '../initialValues'
import { AaregListe, ArbeidsgiverTyper } from '~/components/fagsystem/aareg/AaregTypes'
import { AlertStripeAdvarsel, AlertStripeInfo } from 'nav-frontend-alertstriper'
import { FormikProps } from 'formik'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface ArbeidsforholdToggleProps {
	formikBag: FormikProps<{ aareg: AaregListe }>
}

const ToggleArbeidsgiver = styled(ToggleGruppe)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
`

// Har hardkodet liste over felles Dolly-arbeidsgivere, fordi det tar for lang tid å hente ut fra API
const fellesOrg = [
	'972674818',
	'896929119',
	'839942907',
	'967170232',
	'805824352',
	'907670201',
	'947064649',
]

export const ArbeidsforholdToggle = ({ formikBag }: ArbeidsforholdToggleProps): ReactElement => {
	// const fellesOrg = SelectOptionsOppslag.hentOrgnr().value?.liste
	// TODO hent felles organisasjoner fra api istedenfor hardkodet liste

	const getArbeidsgiverType =
		_get(formikBag.values, 'aareg[0].amelding') || _get(formikBag.values, 'aareg[0].arbeidsforhold')
			? ArbeidsgiverTyper.egen
			: _get(formikBag.values, 'aareg[0].arbeidsgiver.aktoertype') === 'PERS'
			? ArbeidsgiverTyper.privat
			: fellesOrg.some((org) => org === _get(formikBag.values, 'aareg[0].arbeidsgiver.orgnummer'))
			? ArbeidsgiverTyper.felles
			: ArbeidsgiverTyper.fritekst

	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(getArbeidsgiverType)

	const toggleValues = [
		{
			value: ArbeidsgiverTyper.egen,
			label: 'Egen organisasjon',
		},
		{
			value: ArbeidsgiverTyper.felles,
			label: 'Felles organisasjoner',
		},
		{
			value: ArbeidsgiverTyper.fritekst,
			label: 'Skriv inn org.nr.',
		},
		{
			value: ArbeidsgiverTyper.privat,
			label: 'Privat arbeidsgiver',
		},
	]

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)
		if (value === ArbeidsgiverTyper.privat) {
			formikBag.setFieldValue('aareg', [initialAaregPers])
		} else if (value === ArbeidsgiverTyper.felles || value === ArbeidsgiverTyper.fritekst) {
			formikBag.setFieldValue('aareg', [initialAaregOrg])
		} else if (value === ArbeidsgiverTyper.egen) {
			formikBag.setFieldValue('aareg', [initialValues])
		}
	}

	return (
		<ErrorBoundary>
			<LoadableComponent
				onFetch={() =>
					SelectOptionsOppslag.hentVirksomheterFraOrgforvalter().then((response) => {
						return response
					})
				}
				render={(data) => {
					const harEgneOrg = data && data.length > 0 ? true : false
					const orgLink = <a href="/organisasjoner">her</a>
					return (
						<div className="toggle--wrapper">
							<ToggleArbeidsgiver
								onChange={(event) => handleToggleChange(event.target.value)}
								name={'arbeidsforhold'}
							>
								{toggleValues.map((type) => (
									<ToggleKnapp
										key={type.value}
										value={type.value}
										checked={type.value === typeArbeidsgiver}
									>
										{type.label}
									</ToggleKnapp>
								))}
							</ToggleArbeidsgiver>
							{typeArbeidsgiver === ArbeidsgiverTyper.egen ? (
								<>
									{!harEgneOrg && (
										<AlertStripeAdvarsel>
											Du har ingen egne organisasjoner, og kan derfor ikke sende inn A-meldinger for
											person. For å lage dine egne organisasjoner trykk {orgLink}. For å opprette
											person med arbeidsforhold i felles organisasjoner eller andre arbeidsgivere,
											velg en annen kategori ovenfor.
										</AlertStripeAdvarsel>
									)}
									<AmeldingForm formikBag={formikBag} />
								</>
							) : (
								<>
									<AlertStripeInfo>
										For denne typen arbeidsgiver er det ikke mulig å registrere nye attributter som
										sluttårsak, ansettelsesform, endringsdato lønn og fartøy. For å bestille brukere
										med disse attributtene må du bruke egen organisasjon for å opprette A-meldinger.
									</AlertStripeInfo>
									<FormikDollyFieldArray
										name="aareg"
										header="Arbeidsforhold"
										newEntry={
											typeArbeidsgiver === ArbeidsgiverTyper.privat
												? { ...initialArbeidsforholdPers, arbeidsforholdstype: '' }
												: { ...initialArbeidsforholdOrg, arbeidsforholdstype: '' }
										}
										canBeEmpty={false}
									>
										{(path: string, idx: number) => (
											<ArbeidsforholdForm
												path={path}
												key={idx}
												arbeidsforholdIndex={idx}
												formikBag={formikBag}
												erLenket={null}
												arbeidsgiverType={typeArbeidsgiver}
											/>
										)}
									</FormikDollyFieldArray>
								</>
							)}
						</div>
					)
				}}
			/>
		</ErrorBoundary>
	)
}
