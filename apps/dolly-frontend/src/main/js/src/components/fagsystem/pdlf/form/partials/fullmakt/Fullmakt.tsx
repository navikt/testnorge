import * as React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { initialFullmakt } from '@/components/fagsystem/pdlf/form/initialValues'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface FullmaktProps {
	formMethods: UseFormReturn
	path?: string
	eksisterendeNyPerson?: any
}

export const FullmaktForm = ({ formMethods, path, eksisterendeNyPerson = null }: FullmaktProps) => {
	const fullmaktOmraader = SelectOptionsOppslag.hentFullmaktOmraader()
	const fullmaktOptions = SelectOptionsFormat.formatOptions('fullmaktOmraader', fullmaktOmraader)

	return (
		<div className="flexbox--flex-wrap">
			<div className="flexbox--full-width">
				<FormSelect
					name={`${path}.omraader`}
					label="Områder"
					options={fullmaktOptions}
					size="grow"
					isMulti={true}
					isClearable={false}
				/>
			</div>
			<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
			<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />
			<PdlPersonExpander
				nyPersonPath={`${path}.nyFullmektig`}
				eksisterendePersonPath={`${path}.motpartsPersonident`}
				label={'FULLMEKTIG'}
				formMethods={formMethods}
				isExpanded={
					!isEmpty(formMethods.watch(`${path}.nyFullmektig`), ['syntetisk']) ||
					formMethods.watch(`${path}.motpartsPersonident`) !== null
				}
				eksisterendeNyPerson={eksisterendeNyPerson}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</div>
	)
}

export const Fullmakt = ({ formMethods }: FullmaktProps) => {
	return (
		<FormDollyFieldArray
			name="pdldata.person.fullmakt"
			header="Fullmakt"
			newEntry={initialFullmakt}
			canBeEmpty={false}
		>
			{(path: string) => <FullmaktForm formMethods={formMethods} path={path} />}
		</FormDollyFieldArray>
	)
}
