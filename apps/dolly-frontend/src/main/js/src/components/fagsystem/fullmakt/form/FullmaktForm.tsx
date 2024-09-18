import * as React from 'react'
import { useContext, useEffect } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { initialFullmakt } from '@/components/fagsystem/pdlf/form/initialValues'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { validation } from '@/components/fagsystem/skjermingsregister/form/validation'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { useFormContext } from 'react-hook-form'
import { useFullmaktOmraader } from '@/utils/hooks/useFullmakt'

interface FullmaktProps {
	formMethods: UseFormReturn
	path?: string
	eksisterendeNyPerson?: any
}

const mapLegacyFullmaktTilNyFullmakt = (legacyFullmakt: any) => {
	if (!legacyFullmakt) return null
	const { gyldigFraOgMed, gyldigTilOgMed, omraader, fullmektig } = legacyFullmakt
	//TODO: Skrive denne når ny fullmakt er ferdig
	return {
		gyldigFraOgMed,
		gyldigTilOgMed,
		omraader,
		nyFullmektig: fullmektig,
	}
}

const fullmaktAttributter = ['fullmakt', 'pdl.person.fullmakt']

export const Fullmakt = ({ formMethods, path, eksisterendeNyPerson = null }: FullmaktProps) => {
	const legacyFullmakt = formMethods.watch('pdldata.person.fullmakt')
	const opts: any = useContext(BestillingsveilederContext)
	const { omraadeKodeverk } = useFullmaktOmraader()

	const isTestnorgeIdent = opts?.identMaster === 'PDL'

	useEffect(() => {
		mapLegacyFullmaktTilNyFullmakt(legacyFullmakt)
	}, [legacyFullmakt])

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={`${path}.omraade`}
				header="Områder"
				newEntry={{ tema: '', handling: [] }}
			>
				{(path: string) => {
					return (
						<>
							<FormSelect
								name={`${path}.tema`}
								label="Tema"
								options={omraadeKodeverk}
								size="grow"
								isClearable={false}
							/>
							<FormSelect
								name={`${path}.handling`}
								label="Handling"
								options={Options('fullmaktHandling')}
								size="small"
								isClearable={false}
								isMulti={true}
							/>
						</>
					)
				}}
			</FormDollyFieldArray>
			<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
			<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />
			<PdlPersonExpander
				nyPersonPath={`${path}.nyFullmektig`}
				eksisterendePersonPath={`${path}.motpartsPersonident`}
				label={'FULLMEKTIG'}
				formMethods={formMethods}
				isExpanded={
					isTestnorgeIdent ||
					!isEmpty(formMethods.watch(`${path}.nyFullmektig`), ['syntetisk']) ||
					formMethods.watch(`${path}.motpartsPersonident`) !== null
				}
				toggleExpansion={!isTestnorgeIdent}
				eksisterendeNyPerson={eksisterendeNyPerson}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</div>
	)
}

export const FullmaktForm = () => {
	const formMethods = useFormContext()
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
					newEntry={initialFullmakt}
					canBeEmpty={false}
				>
					{(path: string) => <Fullmakt formMethods={formMethods} path={path} />}
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}

FullmaktForm.validation = validation
