import * as React from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { initialFullmakt } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import * as _ from 'lodash-es'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'

interface FullmaktProps {
	formikBag: FormikProps<{}>
	path?: string
	eksisterendeNyPerson?: any
}

export const FullmaktForm = ({ formMethods, path, eksisterendeNyPerson = null }: FullmaktProps) => {
	const fullmaktOmraader = SelectOptionsOppslag.hentFullmaktOmraader()
	const fullmaktOptions = SelectOptionsFormat.formatOptions('fullmaktOmraader', fullmaktOmraader)

	return (
		<div className="flexbox--flex-wrap">
			<div className="flexbox--full-width">
				<FormikSelect
					name={`${path}.omraader`}
					label="Områder"
					options={fullmaktOptions}
					size="grow"
					isMulti={true}
					isClearable={false}
					fastfield={false}
				/>
			</div>
			<DatepickerWrapper>
				<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
				<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />
			</DatepickerWrapper>
			<PdlPersonExpander
				nyPersonPath={`${path}.nyFullmektig`}
				eksisterendePersonPath={`${path}.motpartsPersonident`}
				label={'FULLMEKTIG'}
				formMethods={formMethods}
				isExpanded={
					!isEmpty(_.get(formikBag.values, `${path}.nyFullmektig`), ['syntetisk']) ||
					_.get(formikBag.values, `${path}.motpartsPersonident`) !== null
				}
				eksisterendeNyPerson={eksisterendeNyPerson}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</div>
	)
}

export const Fullmakt = ({ formMethods }: FullmaktProps) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.fullmakt"
			header="Fullmakt"
			newEntry={initialFullmakt}
			canBeEmpty={false}
		>
			{(path: string) => <FullmaktForm formMethods={formMethods} path={path} />}
		</FormikDollyFieldArray>
	)
}
