import * as React from 'react'
import { useContext } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import {
	getInitialSivilstand,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as _ from 'lodash-es'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { Option } from '@/service/SelectOptionsOppslag'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface SivilstandFormTypes {
	formMethods: UseFormReturn
	path?: string
	eksisterendeNyPerson?: Option | null
	identtype?: string
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
	}

	const kanHaRelatertPerson = gyldigeSivilstander.includes(
		_.get(formMethods.getValues(), `${path}.type`),
	)

	return (
		<div className="flexbox--flex-wrap">
			<FormikSelect
				name={`${path}.type`}
				label="Type sivilstand"
				options={Options('sivilstandType')}
				onChange={(selected: any) => handleTypeChange(selected, path)}
				isClearable={false}
			/>
			{_.get(formMethods.getValues(), `${path}.type`) === 'SAMBOER' && (
				<div style={{ marginLeft: '-20px', marginRight: '20px', paddingTop: '27px' }}>
					<Hjelpetekst>
						Samboer eksisterer verken i PDL eller TPS. Personer med denne typen sisvilstand vil
						derfor vises som ugift i fagsystemene.
					</Hjelpetekst>
				</div>
			)}
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.sivilstandsdato`}
					label="Gyldig fra og med"
					disabled={_.get(formMethods.getValues(), `${path}.bekreftelsesdato`) != null}
					fastfield={false}
				/>
				<FormikDatepicker
					name={`${path}.bekreftelsesdato`}
					label="Bekreftelsesdato"
					disabled={
						_.get(formMethods.getValues(), `${path}.sivilstandsdato`) != null ||
						_.get(formMethods.getValues(), `${path}.master`) !== 'PDL' ||
						_.get(formMethods.getValues(), `${path}.type`) === 'SAMBOER'
					}
					fastfield={false}
				/>
			</DatepickerWrapper>
			<FormikCheckbox
				name={`${path}.borIkkeSammen`}
				label="Bor ikke sammen"
				isDisabled={!kanHaRelatertPerson}
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
						!isEmpty(_.get(formMethods.getValues(), `${path}.nyRelatertPerson`), ['syntetisk']) ||
						_.get(formMethods.getValues(), `${path}.relatertVedSivilstand`) !== null
					}
				/>
			)}
			<AvansertForm
				path={path}
				kanVelgeMaster={
					_.get(formMethods.getValues(), `${path}.bekreftelsesdato`) === null && identtype !== 'NPID'
				}
			/>
		</div>
	)
}

export const Sivilstand = ({ formMethods }: SivilstandFormTypes) => {
	const opts = useContext(BestillingsveilederContext)
	return (
		<FormikDollyFieldArray
			name="pdldata.person.sivilstand"
			header="Sivilstand"
			newEntry={getInitialSivilstand(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
			canBeEmpty={false}
		>
			{(path: string) => (
				<SivilstandForm path={path} formMethods={formMethods} identtype={opts?.identtype} />
			)}
		</FormikDollyFieldArray>
	)
}
