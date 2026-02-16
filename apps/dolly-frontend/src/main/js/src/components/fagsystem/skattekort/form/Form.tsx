import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import Panel from '@/components/ui/panel/Panel'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useSkattekortKodeverk } from '@/utils/hooks/useSkattekort'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { addYears, subYears } from 'date-fns'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import {
	ForskuddstrekkForm,
	initialTrekktabell,
} from '@/components/fagsystem/skattekort/form/Forskuddstrekk'
import { validation } from '@/components/fagsystem/skattekort/form/validation'

export const initialArbeidsgiverSkatt = (
	skattekortidentifikator = Math.floor(100000 + Math.random() * 900000), //NOSONAR not used in secure contexts
) => {
	return {
		arbeidsgiveridentifikator: {
			organisasjonsnummer: '',
		},
		arbeidstaker: [
			{
				resultatPaaForespoersel: 'SKATTEKORTOPPLYSNINGER_OK',
				skattekort: {
					utstedtDato: '',
					skattekortidentifikator: skattekortidentifikator,
					forskuddstrekk: [initialTrekktabell],
				},
				tilleggsopplysning: [],
				inntektsaar: new Date().getFullYear(),
			},
		],
	}
}

export const skattekortAttributt = 'skattekort'

export const SkattekortForm = () => {
	const formMethods = useFormContext()

	const { kodeverk: resultatstatus } = useSkattekortKodeverk('RESULTATSTATUS')
	const { kodeverk: tilleggsopplysning } = useSkattekortKodeverk('TILLEGGSOPPLYSNING')

	const handleRemoveEntry = (idx: number) => {
		const skattekortListe = formMethods.watch('skattekort.arbeidsgiverSkatt')
		const filterskattekortListe = skattekortListe?.filter((_, index) => index !== idx)
		formMethods.setValue('skattekort.arbeidsgiverSkatt', filterskattekortListe)
		formMethods.trigger('skattekort.arbeidsgiverSkatt')
	}

	return (
		<Vis attributt={skattekortAttributt}>
			<Panel
				heading="Nav skattekort"
				hasErrors={panelError(skattekortAttributt)}
				iconType="skattekort"
				startOpen={erForsteEllerTest(formMethods.getValues(), [skattekortAttributt])}
			>
				<ErrorBoundary>
					<FormDollyFieldArray
						name="skattekort.arbeidsgiverSkatt"
						header="Skattekort"
						newEntry={initialArbeidsgiverSkatt()}
						canBeEmpty={false}
						handleRemoveEntry={handleRemoveEntry}
					>
						{(path: string) => (
							<>
								<div className="flexbox--flex-wrap">
									<FormSelect
										name={`${path}.arbeidstaker[0].resultatPaaForespoersel`}
										label="Resultat på forespørsel"
										options={resultatstatus}
										defaultValue={'skattekortopplysningerOK'}
										size="large"
										isClearable={false}
									/>
									<FormSelect
										name={`${path}.arbeidstaker[0].inntektsaar`}
										label="Inntektsår"
										options={getYearRangeOptions(subYears(new Date(), 1), addYears(new Date(), 1))}
										size="xsmall"
										isClearable={false}
									/>
									<FormDatepicker
										name={`${path}.arbeidstaker[0].skattekort.utstedtDato`}
										label="Utstedt dato"
									/>
								</div>
								<ForskuddstrekkForm
									formMethods={formMethods}
									path={`${path}.arbeidstaker[0].skattekort`}
								/>
								<FormSelect
									name={`${path}.arbeidstaker[0].tilleggsopplysning`}
									label="Tilleggsopplysning"
									options={tilleggsopplysning}
									size="grow"
									isMulti={true}
								/>
							</>
						)}
					</FormDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

SkattekortForm.validation = validation
