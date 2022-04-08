import * as React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialPdlPerson, initialSivilstand } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import _get from 'lodash/get'
import { isEmpty } from '~/components/fagsystem/pdlf/form/partials/utils'

interface SivilstandForm {
	formikBag: FormikProps<{}>
}

const gyldigeSivilstander = ['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER']

export const Sivilstand = ({ formikBag }: SivilstandForm) => {
	const handleTypeChange = (selected: any, path: string) => {
		formikBag.setFieldValue(`${path}.type`, selected.value)
		if (!gyldigeSivilstander.includes(selected.value)) {
			formikBag.setFieldValue(`${path}.relatertVedSivilstand`, null)
			formikBag.setFieldValue(`${path}.nyRelatertPerson`, initialPdlPerson)
		}
	}

	return (
		<FormikDollyFieldArray
			name="pdldata.person.sivilstand"
			header="Sivilstand"
			newEntry={initialSivilstand}
			canBeEmpty={false}
		>
			{(path: string) => {
				const kanHaRelatertPerson = gyldigeSivilstander.includes(
					_get(formikBag.values, `${path}.type`)
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
						<FormikDatepicker
							name={`${path}.sivilstandsdato`}
							label="Gyldig fra og med"
							disabled={_get(formikBag.values, `${path}.bekreftelsesdato`) != null}
							fastfield={false}
						/>
						<FormikDatepicker
							name={`${path}.bekreftelsesdato`}
							label="Bekreftelsesdato"
							disabled={
								_get(formikBag.values, `${path}.sivilstandsdato`) != null ||
								_get(formikBag.values, `${path}.master`) !== 'PDL'
							}
							fastfield={false}
						/>
						<FormikCheckbox name={`${path}.borIkkeSammen`} label="Bor ikke sammen" checkboxMargin />
						{kanHaRelatertPerson && (
							<PdlPersonExpander
								nyPersonPath={`${path}.nyRelatertPerson`}
								eksisterendePersonPath={`${path}.relatertVedSivilstand`}
								label={'PERSON RELATERT TIL'}
								formikBag={formikBag}
								isExpanded={
									!isEmpty(_get(formikBag.values, `${path}.nyRelatertPerson`)) ||
									_get(formikBag.values, `${path}.relatertVedSivilstand`) !== null
								}
							/>
						)}
						<AvansertForm
							path={path}
							kanVelgeMaster={_get(formikBag.values, `${path}.bekreftelsesdato`) === null}
						/>
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
