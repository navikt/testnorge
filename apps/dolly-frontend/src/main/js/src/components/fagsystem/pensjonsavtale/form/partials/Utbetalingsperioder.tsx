import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { initialUtbetalingsperiodeVerdier } from '@/components/fagsystem/pensjonsavtale/initalValues'

export const UtbetalingsperioderForm = ({ path }: any) => {
	return (
		<FormDollyFieldArray
			name={path}
			header="Utbetalingsperioder"
			newEntry={initialUtbetalingsperiodeVerdier}
			nested
		>
			{(path: any, idx: React.Key) => (
				<React.Fragment key={idx}>
					<div className="flexbox--flex-wrap">
						<FormTextInput name={`${path}.startAlderAar`} label="Startalder År" type="number" />
						<FormSelect
							name={`${path}.startAlderMaaneder`}
							label="Start Måned"
							size={'medium'}
							options={Options('maanedsvelger')}
						/>
						<FormTextInput name={`${path}.sluttAlderAar`} label="Sluttalder År" type="number" />
						<FormSelect
							name={`${path}.sluttAlderMaaneder`}
							label="Slutt Måned"
							size={'medium'}
							options={Options('maanedsvelger')}
						/>
						<FormTextInput
							name={`${path}.aarligUtbetaling`}
							label="Årlig utbetaling"
							type="number"
						/>
						<FormSelect
							name={`${path}.grad`}
							label="Grad"
							size={'medium'}
							options={Options('avtaleperiodeGrad')}
						/>
					</div>
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
