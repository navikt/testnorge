import * as React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialSivilstand } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import _get from 'lodash/get'
import { isEmpty } from '~/components/fagsystem/pdlf/form/partials/utils'

interface SivilstandForm {
	formikBag: FormikProps<{}>
}

export const Sivilstand = ({ formikBag }: SivilstandForm) => (
	<FormikDollyFieldArray
		name="pdldata.person.sivilstand"
		header="Sivilstand"
		newEntry={initialSivilstand}
		canBeEmpty={false}
	>
		{(path: string) => {
			return (
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name={`${path}.type`}
						label="Type sivilstand"
						options={Options('sivilstandType')}
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
					<AvansertForm
						path={path}
						kanVelgeMaster={_get(formikBag.values, `${path}.bekreftelsesdato`) === null}
					/>
				</div>
			)
		}}
	</FormikDollyFieldArray>
)
