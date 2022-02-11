import * as React from 'react'
import { FormikProps } from 'formik'
import { initialNyIdent } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { Option } from '~/service/SelectOptionsOppslag'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import _get from 'lodash/get'

interface NyIdentForm {
	formikBag: FormikProps<{}>
	identOptions: Array<Option>
}

export const NyIdent = ({ formikBag, identOptions }: NyIdentForm) => (
	<FormikDollyFieldArray
		name="pdldata.person.nyident"
		header="Ny identitet"
		newEntry={initialNyIdent}
		canBeEmpty={false}
	>
		{(path: string) => {
			return (
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name={`${path}.eksisterendeIdent`}
						label="Eksisterende ident"
						options={identOptions}
						size={'xlarge'}
					/>
					<PdlPersonExpander
						path={path}
						label="NY IDENTITET"
						formikBag={formikBag}
						kanSettePersondata={_get(formikBag.values, `${path}.eksisterendeIdent`) === null}
						erNyIdent
					/>
					<AvansertForm path={path} kanVelgeMaster={true} />
				</div>
			)
		}}
	</FormikDollyFieldArray>
)
