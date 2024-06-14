import * as React from 'react'
import { useContext } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import {
	getInitialSivilstand,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { Option } from '@/service/SelectOptionsOppslag'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface SivilstandFormTypes {
	formMethods: UseFormReturn
	path?: string
	eksisterendeNyPerson?: Option | null
	identtype?: string
	ident?: string
}

const gyldigeSivilstander = [
	'GIFT',
	'REGISTRERT_PARTNER',
	'SEPARERT',
	'SEPARERT_PARTNER',
	'SAMBOER',
]

export const SivilstandForm = ({
	path,
	formMethods,
	eksisterendeNyPerson = null,
	identtype,
	ident,
}: SivilstandFormTypes) => {
	const handleTypeChange = (selected: any, path: string) => {
		formMethods.setValue(`${path}.type`, selected.value)
		if (!gyldigeSivilstander.includes(selected.value)) {
			formMethods.setValue(`${path}.borIkkeSammen`, false)
			formMethods.setValue(`${path}.relatertVedSivilstand`, null)
			formMethods.setValue(`${path}.nyRelatertPerson`, initialPdlPerson)
		}
		if (selected.value === 'SAMBOER') {
			formMethods.setValue(`${path}.bekreftelsesdato`, null)
		}
		formMethods.trigger()
	}

	const opts = useContext(BestillingsveilederContext)
	const identMaster = opts?.identMaster || (parseInt(ident?.charAt(2)) >= 8 ? 'PDL' : 'PDLF')

	const isTestnorgeIdent = identMaster === 'PDL'
	const kanVelgeMaster = !isTestnorgeIdent && (opts?.identtype !== 'NPID' || identtype !== 'NPID')

	const kanHaRelatertPerson = gyldigeSivilstander.includes(formMethods.watch(`${path}.type`))

	return (
		<div className="flexbox--flex-wrap sivilstand-form">
			<FormSelect
				name={`${path}.type`}
				label="Type sivilstand"
				options={Options('sivilstandType')}
				onChange={(selected: any) => handleTypeChange(selected, path)}
				isClearable={false}
			/>
			{formMethods.watch(`${path}.type`) === 'SAMBOER' && (
				<div style={{ marginLeft: '-20px', marginRight: '20px', paddingTop: '27px' }}>
					<Hjelpetekst>
						Samboer eksisterer ikke i PDL. Personer med denne typen sisvilstand vil derfor vises som
						ugift i fagsystemene.
					</Hjelpetekst>
				</div>
			)}
			<FormDatepicker
				name={`${path}.sivilstandsdato`}
				label="Gyldig fra og med"
				disabled={formMethods.watch(`${path}.bekreftelsesdato`) != null}
			/>
			<FormDatepicker
				name={`${path}.bekreftelsesdato`}
				label="Bekreftelsesdato"
				disabled={
					formMethods.watch(`${path}.sivilstandsdato`) != null ||
					formMethods.watch(`${path}.master`) !== 'PDL' ||
					formMethods.watch(`${path}.type`) === 'SAMBOER'
				}
			/>
			<FormCheckbox
				name={`${path}.borIkkeSammen`}
				label="Bor ikke sammen"
				isDisabled={!kanHaRelatertPerson}
				vis={!isTestnorgeIdent}
				checkboxMargin
			/>
			{kanHaRelatertPerson && (
				<PdlPersonExpander
					nyPersonPath={`${path}.nyRelatertPerson`}
					eksisterendePersonPath={`${path}.relatertVedSivilstand`}
					eksisterendeNyPerson={eksisterendeNyPerson}
					label={'PERSON RELATERT TIL'}
					formMethods={formMethods}
					isExpanded={
						isTestnorgeIdent ||
						!isEmpty(formMethods.watch(`${path}.nyRelatertPerson`), ['syntetisk']) ||
						formMethods.watch(`${path}.relatertVedSivilstand`) !== null
					}
					toggleExpansion={identMaster != 'PDL'}
				/>
			)}
			<AvansertForm
				path={path}
				kanVelgeMaster={formMethods.watch(`${path}.bekreftelsesdato`) === null && kanVelgeMaster}
			/>
		</div>
	)
}

export const Sivilstand = ({ formMethods }: SivilstandFormTypes) => {
	// @ts-ignore
	const { identtype, identMaster } = useContext(BestillingsveilederContext)
	const initiellMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'

	return (
		<FormDollyFieldArray
			name="pdldata.person.sivilstand"
			header="Sivilstand"
			newEntry={getInitialSivilstand(initiellMaster)}
			canBeEmpty={false}
		>
			{(path: string) => <SivilstandForm path={path} formMethods={formMethods} />}
		</FormDollyFieldArray>
	)
}
