import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { initialUtbetalingsperiode } from '@/components/fagsystem/pensjonsavtale/initalValues'

export const UtbetalingsperioderForm = ({ path }: any) => {
	return (
		<FormDollyFieldArray
			name={path}
			header="Utbetalingsperioder"
			newEntry={initialUtbetalingsperiode}
			nested
			canBeEmpty={false}
		>
			{(path: any, idx: React.Key) => (
				<React.Fragment key={idx}>
					<div className="flexbox--flex-wrap">
						<FormTextInput name={`${path}.startAlderAar`} label="Startalder År" type="number" />
						<FormSelect
							name={`${path}.startAlderMaaned`}
							label="Start Måned"
							size={'medium'}
							options={Options('maanedsvelger')}
							isClearable={false}
						/>
						<FormTextInput name={`${path}.sluttAlderAar`} label="Sluttalder År" type="number" />
						<FormSelect
							name={`${path}.sluttAlderMaaned`}
							label="Slutt Måned"
							size={'medium'}
							options={Options('maanedsvelger')}
						/>
						<FormTextInput
							name={`${path}.aarligUtbetaling`}
							label="Årlig utbetaling"
							type="number"
						/>
					</div>
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
