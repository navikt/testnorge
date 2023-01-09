import * as React from 'react'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialDoedfoedtBarn } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

interface DoedfoedtBarnForm {
	formikBag: FormikProps<{}>
}

export const DoedfoedtBarn = ({ formikBag }: DoedfoedtBarnForm) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.doedfoedtBarn"
			header={'Dødfødt barn'}
			newEntry={initialDoedfoedtBarn}
			canBeEmpty={false}
		>
			{(path: string) => {
				return (
					<div className="flexbox--flex-wrap">
						<FormikDatepicker
							name={`${path}.dato`}
							label="Dødsdato"
							fastfield={false}
							maxDate={new Date()}
						/>
						<AvansertForm
							path={path}
							kanVelgeMaster={_.get(formikBag.values, `${path}.bekreftelsesdato`) === null}
						/>
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
