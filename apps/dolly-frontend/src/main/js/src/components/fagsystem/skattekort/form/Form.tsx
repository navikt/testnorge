import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React, { useContext } from 'react'
import Panel from '@/components/ui/panel/Panel'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useSkattekortKodeverk } from '@/utils/hooks/useSkattekort'
import { getYearRangeOptions } from '@/utils/DataFormatter'
import { subYears } from 'date-fns'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import {
	ForskuddstrekkForm,
	initialTrekktabell,
} from '@/components/fagsystem/skattekort/form/Forskuddstrekk'
import { validation } from '@/components/fagsystem/skattekort/form/validation'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { SkattekortData } from '@/components/fagsystem/skattekort/visning/Visning'
import { ExpansionCard } from '@navikt/ds-react'
import styled from 'styled-components'

const StyledExpansionCard = styled.div`
	margin-bottom: 20px;
	.aksel-body-long--small {
		line-height: unset;
	}
`

export const initialArbeidsgiverSkatt = () => {
	return {
		arbeidstaker: [
			{
				resultatPaaForespoersel: 'SKATTEKORTOPPLYSNINGER_OK',
				skattekort: {
					utstedtDato: new Date().toISOString(),
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
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const { kodeverk: resultatstatus } = useSkattekortKodeverk('RESULTATSTATUS')
	const { kodeverk: tilleggsopplysning } = useSkattekortKodeverk('TILLEGGSOPPLYSNING')

	const handleRemoveEntry = (idx: number) => {
		const skattekortListe = formMethods.watch('skattekort.arbeidsgiverSkatt')
		const filterskattekortListe = skattekortListe?.filter((_, index) => index !== idx)
		formMethods.setValue('skattekort.arbeidsgiverSkatt', filterskattekortListe)
		formMethods.trigger('skattekort.arbeidsgiverSkatt')
	}

	const isResultatOK = (index: number): boolean => {
		const arbeidstaker = formMethods.watch('skattekort.arbeidsgiverSkatt')[index]?.arbeidstaker?.[0]
		return arbeidstaker?.resultatPaaForespoersel === 'SKATTEKORTOPPLYSNINGER_OK'
	}

	const isTilleggsopplysning = (index: number): boolean => {
		const arbeidstaker = formMethods.watch('skattekort.arbeidsgiverSkatt')[index]?.arbeidstaker?.[0]
		return arbeidstaker?.tilleggsopplysning?.find(
			(opl: string) => opl === 'OPPHOLD_PAA_SVALBARD' || opl === 'KILDESKATT_PAA_PENSJON',
		)
	}

	const onBlurResultatPaaForespoersel = (path: string, index: number) => {
		if (!isResultatOK(index)) {
			formMethods.setValue(`${path}.arbeidstaker[0].tilleggsopplysning`, [])
			formMethods.setValue(`${path}.arbeidstaker[0].skattekort.utstedtDato`, null)
			formMethods.setValue(`${path}.arbeidstaker[0].skattekort.forskuddstrekk`, [])
		}
	}

	const onBlurTillegsinformasjon = (path: string, index: number) => {
		if (isTilleggsopplysning(index)) {
			formMethods.setValue(`${path}.arbeidstaker[0].skattekort.forskuddstrekk`, [])
		}
	}

	const upperYearInntektsaar = (): number => {
		const currentYear = new Date().getFullYear()
		const boundaryDate = new Date(currentYear + '-12-15') // 15. desember i inneværende år
		return new Date() < boundaryDate ? currentYear : currentYear + 1
	}

	const personFoerLeggTil = opts?.personFoerLeggTil
	const eksisterendeSkattekort = personFoerLeggTil?.skattekort

	return (
		<Vis attributt={skattekortAttributt}>
			<Panel
				heading="Nav skattekort"
				hasErrors={panelError(skattekortAttributt)}
				iconType="skattekort"
				startOpen={erForsteEllerTest(formMethods.getValues(), [skattekortAttributt])}
			>
				<ErrorBoundary>
					{eksisterendeSkattekort && eksisterendeSkattekort.length > 0 && (
						<StyledExpansionCard>
							<ExpansionCard size="small" aria-label="Eksisterende skattekort på person">
								<ExpansionCard.Header>
									<ExpansionCard.Title>Eksisterende skattekort på person</ExpansionCard.Title>
								</ExpansionCard.Header>
								<ExpansionCard.Content>
									<SkattekortData liste={eksisterendeSkattekort} />
								</ExpansionCard.Content>
							</ExpansionCard>
						</StyledExpansionCard>
					)}
					<FormDollyFieldArray
						name="skattekort.arbeidsgiverSkatt"
						header="Skattekort"
						newEntry={initialArbeidsgiverSkatt()}
						canBeEmpty={false}
						handleRemoveEntry={handleRemoveEntry}
					>
						{(path: string, index: number) => (
							<>
								<div className="flexbox--flex-wrap">
									<FormSelect
										name={`${path}.arbeidstaker[0].resultatPaaForespoersel`}
										label="Resultat på forespørsel"
										options={resultatstatus}
										size="large"
										isClearable={false}
										onBlur={() => onBlurResultatPaaForespoersel(path, index)}
									/>
									<FormSelect
										name={`${path}.arbeidstaker[0].inntektsaar`}
										label="Inntektsår"
										options={getYearRangeOptions(subYears(new Date(), 1), upperYearInntektsaar())}
										size="xsmall"
										isClearable={false}
									/>
									{isResultatOK(index) && (
										<FormDatepicker
											name={`${path}.arbeidstaker[0].skattekort.utstedtDato`}
											label="Utstedt dato"
										/>
									)}
								</div>
								{isResultatOK(index) && (
									<FormSelect
										name={`${path}.arbeidstaker[0].tilleggsopplysning`}
										label="Tilleggsopplysning"
										options={tilleggsopplysning}
										size="grow"
										isMulti={true}
										onBlur={() => onBlurTillegsinformasjon(path, index)}
									/>
								)}
								{isResultatOK(index) && !isTilleggsopplysning(index) && (
									<ForskuddstrekkForm
										formMethods={formMethods}
										path={`${path}.arbeidstaker[0].skattekort`}
									/>
								)}
							</>
						)}
					</FormDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

SkattekortForm.validation = validation
