import * as React from 'react'
import { FormikProps } from 'formik'
import { initialNyIdent } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { PdlPersonForm } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { Option } from '~/service/SelectOptionsOppslag'

interface NyIdentForm {
	formikBag: FormikProps<{}>
	identOptions: Array<Option>
}

export const NyIdent = ({ formikBag, identOptions }: NyIdentForm) => (
	<FormikDollyFieldArray
		name="pdldata.person.nyIdent"
		header="Ny identitet"
		newEntry={initialNyIdent}
		canBeEmpty={false}
	>
		{(path: string) => {
			return (
				<div className="flexbox--flex-wrap">
					{/*TODO: Endre name når api er oppdatert*/}
					<FormikSelect
						name={`${path}.eksisterendeIdent`}
						label="Eksisterende ident"
						options={identOptions}
						size={'xlarge'}
					/>
					<PdlPersonForm path={path} formikBag={formikBag} erNyIdent />
					<AvansertForm path={path} kanVelgeMaster={true} />
				</div>
			)
		}}
	</FormikDollyFieldArray>
)
