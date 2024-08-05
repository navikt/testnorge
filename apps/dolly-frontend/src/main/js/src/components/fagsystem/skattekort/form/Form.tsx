import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import Panel from '@/components/ui/panel/Panel'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useSkattekortKodeverk } from '@/utils/hooks/useSkattekort'

export const initialArbeidsgiverSkatt = {
	arbeidsgiveridentifikator: {
		organisasjonsnummer: '',
		personidentifikator: '',
	},
	arbeidstaker: [
		{
			arbeidstakeridentifikator: '',
			resultatPaaForespoersel: '',
			skattekort: {
				utstedtDato: '',
				skattekortidentifikator: null,
				trekktype: [
					{
						forskuddstrekk: {
							trekkode: '',
						},
						frikort: {
							trekkode: '',
							frikortbeloep: null,
						},
						trekktabell: {
							trekkode: '',
							tabelltype: '',
							tabellnummer: '',
							prosentsats: null,
							antallMaanederForTrekk: null,
						},
						trekkprosent: {
							trekkode: '',
							prosentsats: null,
							antallMaanederForTrekk: null,
						},
					},
				],
			},
			tilleggsopplysning: '',
			inntektsaar: null,
		},
	],
}

export const skattekortAttributt = 'skattekort'

export const SkattekortForm = () => {
	const formMethods = useFormContext()

	const { kodeverk: resultatstatus } = useSkattekortKodeverk('RESULTATSTATUS')

	return (
		<Vis attributt={skattekortAttributt}>
			<Panel
				heading="Skattekort"
				hasErrors={panelError(skattekortAttributt)}
				iconType="skattekort"
				startOpen={erForsteEllerTest(formMethods.getValues(), [skattekortAttributt])}
			>
				<ErrorBoundary>
					<FormDollyFieldArray
						name="skattekort.arbeidsgiverSkatt"
						header="Skattekort"
						newEntry={initialArbeidsgiverSkatt}
						canBeEmpty={false}
					>
						{(path: string) => (
							<div className="flexbox--flex-wrap">
								<FormSelect
									name={`${path}.arbeidstaker[0].resultatPaaForespoersel`}
									label="Resultat på forespørsel"
									options={resultatstatus}
									size="large"
									isClearable={false}
								/>
							</div>
						)}
					</FormDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}
