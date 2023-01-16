import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialPdlPerson, initialSivilstand } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as _ from 'lodash-es'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

interface SivilstandForm {
	formikBag: FormikProps<{}>
}

const gyldigeSivilstander = [
	'GIFT',
	'REGISTRERT_PARTNER',
	'SEPARERT',
	'SEPARERT_PARTNER',
	'SAMBOER',
]

export const SivilstandForm = ({ path, formikBag }) => {
	const handleTypeChange = (selected: any, path: string) => {
		formikBag.setFieldValue(`${path}.type`, selected.value)
		if (!gyldigeSivilstander.includes(selected.value)) {
			formikBag.setFieldValue(`${path}.borIkkeSammen`, false)
			formikBag.setFieldValue(`${path}.relatertVedSivilstand`, null)
			formikBag.setFieldValue(`${path}.nyRelatertPerson`, initialPdlPerson)
		}
	}

	const kanHaRelatertPerson = gyldigeSivilstander.includes(_.get(formikBag.values, `${path}.type`))

	return (
		<div className="flexbox--flex-wrap">
			<FormikSelect
				name={`${path}.type`}
				label="Type sivilstand"
				options={Options('sivilstandType')}
				onChange={(selected: any) => handleTypeChange(selected, path)}
				isClearable={false}
			/>
			{_.get(formikBag.values, `${path}.type`) === 'SAMBOER' && (
				<div style={{ marginLeft: '-20px', marginRight: '20px', paddingTop: '27px' }}>
					<Hjelpetekst>
						Samboer eksisterer verken i PDL eller TPS. Personer med denne typen sisvilstand vil
						derfor vises som ugift i fagsystemene.
					</Hjelpetekst>
				</div>
			)}
			<FormikDatepicker
				name={`${path}.sivilstandsdato`}
				label="Gyldig fra og med"
				disabled={_.get(formikBag.values, `${path}.bekreftelsesdato`) != null}
				fastfield={false}
			/>
			<FormikDatepicker
				name={`${path}.bekreftelsesdato`}
				label="Bekreftelsesdato"
				disabled={
					_.get(formikBag.values, `${path}.sivilstandsdato`) != null ||
					_.get(formikBag.values, `${path}.master`) !== 'PDL'
				}
				fastfield={false}
			/>
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
					label={'PERSON RELATERT TIL'}
					formikBag={formikBag}
					isExpanded={
						!isEmpty(_.get(formikBag.values, `${path}.nyRelatertPerson`), ['syntetisk']) ||
						_.get(formikBag.values, `${path}.relatertVedSivilstand`) !== null
					}
				/>
			)}
			<AvansertForm
				path={path}
				kanVelgeMaster={_.get(formikBag.values, `${path}.bekreftelsesdato`) === null}
			/>
		</div>
	)
}

export const Sivilstand = ({ formikBag }: SivilstandForm) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.sivilstand"
			header="Sivilstand"
			newEntry={initialSivilstand}
			canBeEmpty={false}
		>
			{(path: string) => <SivilstandForm path={path} formikBag={formikBag} />}
		</FormikDollyFieldArray>
	)
}
