import * as React from 'react'
import { useContext, useEffect } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { initialFullmakt, initialPdlPerson } from '@/components/fagsystem/pdlf/form/initialValues'
import { UseFormReturn } from 'react-hook-form/dist/types'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { useFullmaktOmraader } from '@/utils/hooks/useFullmakt'
import { Omraade } from '@/components/fagsystem/fullmakt/FullmaktType'
import { Option } from '@/service/SelectOptionsOppslag'
import Loading from '@/components/ui/loading/Loading'
import { validation } from '@/components/fagsystem/fullmakt/form/validation'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { DisplayFormError } from '@/components/ui/toast/DisplayFormError'

interface FullmaktProps {
	formMethods: UseFormReturn
	path: string
	opts?: any
	eksisterendeNyPerson?: any
}

const alleHandlinger = ['LES', 'SKRIV', 'KOMMUNISER']

const mapLegacyFullmaktTilNyFullmakt = (
	legacyFullmakt: any,
	formMethods: UseFormReturn,
	omraadeKodeverk: Option[],
) => {
	if (!legacyFullmakt || legacyFullmakt.length === 0) {
		return null
	}

	const nyeFullmakter = legacyFullmakt
		.filter((gammelFullmakt) => gammelFullmakt.omraader)
		.map((gammelFullmakt) => ({
			omraader: undefined, //Navn på feltet er endret i ny ekstern fullmakt
			gyldigFraOgMed: gammelFullmakt.gyldigFraOgMed,
			gyldigTilOgMed: gammelFullmakt.gyldigTilOgMed,
			fullmektig: gammelFullmakt.motpartsPersonident,
			omraade: gammelFullmakt.omraader.map((legacyOmraade, index) => {
				if (legacyOmraade === '*') {
					return { tema: 'AAP', handling: alleHandlinger }
				}
				if (omraadeKodeverk.filter((option) => option.value === legacyOmraade)?.length === 0) {
					console.warn(
						'Fant ikke gammel kodeverk-verdi fra denne malen i nytt kodeverk. Velger annen verdi',
					)
					return {
						tema: omraadeKodeverk[omraadeKodeverk.length - index]?.value,
						handling: alleHandlinger,
					}
				}
				return {
					tema: legacyOmraade,
					handling: alleHandlinger,
				}
			}),
		}))
	formMethods.setValue('fullmakt', nyeFullmakter)
	formMethods.setValue('pdldata.person.fullmakt', [{ nyfullMektig: initialPdlPerson }])
}

export const fullmaktAttributter = ['fullmakt', 'pdldata.person.fullmakt']

const hasUserInput = (formMethods: UseFormReturn) => {
	const fullmaktArray = formMethods.watch('fullmakt')
	if (!fullmaktArray || fullmaktArray.length === 0) {
		return false
	}
	if (fullmaktArray.length > 1) {
		return true
	}
	const fullmaktValues = fullmaktArray[0]
	return (
		fullmaktValues.gyldigFraOgMed ||
		fullmaktValues.gyldigTilOgMed ||
		fullmaktValues.omraade?.[0]?.tema !== ''
	)
}

const getFullmaktTemaLabel = (temaValue: string, omraadeKodeverk: Option[]) => {
	if (!temaValue || !omraadeKodeverk || omraadeKodeverk.length === 0) {
		return null
	}
	const valgtOption = omraadeKodeverk.filter((option) => option.value === temaValue)?.[0]
	return valgtOption ? `${valgtOption.label}` : temaValue
}

export const Fullmakt = ({
	formMethods,
	path,
	opts,
	eksisterendeNyPerson = null,
}: FullmaktProps) => {
	const legacyFullmakt = formMethods.watch('pdldata.person.fullmakt')
	const { omraadeKodeverk, loading } = useFullmaktOmraader()

	useEffect(() => {
		if (!hasUserInput(formMethods)) {
			mapLegacyFullmaktTilNyFullmakt(legacyFullmakt, formMethods, omraadeKodeverk)
		}
	}, [])

	if (loading) {
		return <Loading label={'Henter fullmakter og kodeverk...'} />
	}

	const isTestnorgeIdent = opts?.identMaster === 'PDL'
	const chosenTemaValues =
		(path && formMethods.watch(path)?.omraade?.map((omraade: Omraade) => omraade.tema)) || []

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={`${path}.omraade`}
				header="Område"
				canBeEmpty={false}
				newEntry={{ tema: '', handling: [] }}
				nested
			>
				{(path: string) => (
					<>
						<FormSelect
							name={`${path}.tema`}
							label="Tema"
							placeholder={
								getFullmaktTemaLabel(formMethods.watch(`${path}.tema`), omraadeKodeverk) ||
								'Velg ...'
							}
							options={omraadeKodeverk.filter(
								(option: Option) => !chosenTemaValues.includes(option.value),
							)}
							size="grow"
							isClearable={false}
							normalFontPlaceholder={true}
						/>
						<FormSelect
							name={`${path}.handling`}
							label="Handling"
							options={Options('fullmaktHandling')}
							size="xlarge"
							onChange={(val: Option[]) => {
								formMethods.setValue(
									`${path}.handling`,
									val?.some((opt) => opt.value?.includes('*'))
										? alleHandlinger
										: val.map((opt) => opt.value),
								)
								formMethods.trigger(path)
							}}
							isClearable={true}
							isMulti={true}
						/>
					</>
				)}
			</FormDollyFieldArray>
			<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
			<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />
			<PdlPersonExpander
				path={path}
				nyPersonPath={`${path}.nyFullmektig`}
				eksisterendePersonPath={`${path}.fullmektig`}
				fullmektigsNavnPath={`${path}.fullmektigsNavn`}
				label={'FULLMEKTIG'}
				formMethods={formMethods}
				isExpanded={
					isTestnorgeIdent ||
					!isEmpty(formMethods.watch(`${path}.nyFullmektig`), ['syntetisk']) ||
					!!formMethods.watch(`${path}.fullmektig`)
				}
				toggleExpansion={!isTestnorgeIdent}
				eksisterendeNyPerson={eksisterendeNyPerson}
			/>
			<DisplayFormError path={`${path}.omraade`} />
		</div>
	)
}

export const FullmaktForm = () => {
	const formMethods = useFormContext()
	const fullmaktValues = formMethods.watch('fullmakt')
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const val = formMethods.watch(fullmaktAttributter)
	const { identtype, identMaster } = opts

	if ((!fullmaktValues || fullmaktValues?.length === 0) && val.some((v) => v)) {
		formMethods.setValue('fullmakt', [initialFullmakt])
	}

	return (
		<Vis attributt={fullmaktAttributter}>
			<Panel
				heading="Fullmakt"
				hasErrors={panelError(fullmaktAttributter)}
				iconType="fullmakt"
				startOpen={erForsteEllerTest(formMethods.getValues(), fullmaktAttributter)}
			>
				<FormDollyFieldArray
					name="fullmakt"
					header="Fullmakt"
					newEntry={{
						...initialFullmakt,
						nyFullmektig: initialPdlPerson,
						master: identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG',
					}}
					canBeEmpty={false}
				>
					{(path: string) => <Fullmakt formMethods={formMethods} path={path} opts={opts} />}
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}

FullmaktForm.validation = validation
