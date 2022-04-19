import * as React from 'react'
import { FormikProps } from 'formik'
import { initialNyIdent } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import _get from 'lodash/get'
import { isEmpty } from '~/components/fagsystem/pdlf/form/partials/utils'

interface NyIdentForm {
	formikBag: FormikProps<{}>
}

export const NyIdent = ({ formikBag }: NyIdentForm) => (
	<FormikDollyFieldArray
		name="pdldata.person.nyident"
		header="Ny identitet"
		newEntry={initialNyIdent}
		canBeEmpty={false}
	>
		{(path: string) => {
			const nyIdentValg = Object.keys(_get(formikBag.values, path))
				.filter((key) => key !== 'eksisterendeIdent' && key !== 'kilde' && key !== 'master')
				.reduce((obj, key) => {
					obj[key] = _get(formikBag.values, path)[key]
					return obj
				}, {})

			return (
				<div className="flexbox--flex-wrap">
					<PdlPersonExpander
						nyPersonPath={path}
						eksisterendePersonPath={`${path}.eksisterendeIdent`}
						label="NY IDENTITET"
						formikBag={formikBag}
						nyIdentValg={nyIdentValg}
						isExpanded={
							!isEmpty(nyIdentValg) || _get(formikBag.values, `${path}.eksisterendeIdent`) !== null
						}
					/>
					<AvansertForm path={path} kanVelgeMaster={true} />
				</div>
			)
		}}
	</FormikDollyFieldArray>
)
