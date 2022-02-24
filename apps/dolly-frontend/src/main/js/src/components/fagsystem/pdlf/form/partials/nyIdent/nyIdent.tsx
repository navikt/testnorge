import * as React from 'react'
import { FormikProps } from 'formik'
import { initialNyIdent } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { Option } from '~/service/SelectOptionsOppslag'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import _get from 'lodash/get'
import Loading from '~/components/ui/loading/Loading'
import { isEmpty } from '~/components/fagsystem/pdlf/form/partials/utils'

interface NyIdentForm {
	formikBag: FormikProps<{}>
	identOptions: Array<Option>
	loadingOptions: boolean
}

export const NyIdent = ({ formikBag, identOptions, loadingOptions }: NyIdentForm) => (
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
					{loadingOptions && <Loading label="Henter valg for eksisterende ident..." />}
					{identOptions?.length > 0 && (
						<FormikSelect
							name={`${path}.eksisterendeIdent`}
							label="Eksisterende ident"
							options={identOptions}
							size={'xlarge'}
						/>
					)}
					<PdlPersonExpander
						path={path}
						label="NY IDENTITET"
						formikBag={formikBag}
						kanSettePersondata={_get(formikBag.values, `${path}.eksisterendeIdent`) === null}
						erNyIdent
						isExpanded={
							(!loadingOptions && (!identOptions || identOptions?.length < 1)) ||
							!isEmpty(nyIdentValg)
						}
					/>
					<AvansertForm path={path} kanVelgeMaster={true} />
				</div>
			)
		}}
	</FormikDollyFieldArray>
)
