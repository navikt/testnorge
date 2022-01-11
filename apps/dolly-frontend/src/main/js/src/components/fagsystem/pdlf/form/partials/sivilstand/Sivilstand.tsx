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

interface SivilstandForm {
	formikBag: FormikProps<{}>
}

export const Sivilstand = ({ formikBag }: SivilstandForm) => {
	return (
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
						<FormikDatepicker name={`${path}.sivilstandsdato`} label="Gyldig fra og med" />
						<FormikDatepicker name={`${path}.bekreftelsesdato`} label="Bekreftelsesdato" />
						<FormikCheckbox name={`${path}.borIkkeSammen`} label="Bor ikke sammen" checkboxMargin />
						<FormikSelect
							name={`${path}.relatertVedSivilstand`}
							label="Person relatert til"
							options={[{ value: 'TEST', label: 'Test' }]}
							size={'large'}
						/>
						<PdlPersonExpander
							path={`${path}.nyRelatertPerson`}
							label={'PERSON RELATERT TIL'}
							formikBag={formikBag}
						/>
						<AvansertForm path={path} kanVelgeMaster={true} />
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
