import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'
import {
	initialUtbetalingsperiode,
	initialUtbetalingsperiodeVerdier,
} from '@/components/fagsystem/pensjonsavtale/initalValues'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

export const UtbetalingsperioderForm = ({ formMethods }) => {
	const perioderPath = 'pensjonforvalter.pensjonsavtale.utbetalingsperioder'

	return (
		<Vis attributt={perioderPath}>
			<FormDollyFieldArray
				name={perioderPath}
				header="Utbetalingsperioder"
				newEntry={initialUtbetalingsperiodeVerdier}
				buttonText="Utbetalingsperiode"
				nested
			>
				{(perioderPath) => (
					<>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${perioderPath}.startAlderAar`}
								label="Startalder År"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${perioderPath}.startAlderMaaneder`}
								label="Startalder Måneder"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${perioderPath}.sluttAlderAar`}
								label="Sluttalder År"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${perioderPath}.sluttAlderMaaneder`}
								label="Sluttalder Måneder"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput
								name={`${perioderPath}.aarligUtbetaling`}
								label="Årlig utbetaling"
								type="number"
							/>
						</div>
						<div className="flexbox--flex-wrap">
							<FormTextInput name={`${perioderPath}.grad`} label="Grad" type="number" />
						</div>
						<EraseFillButtons
							formMethods={formMethods}
							path={perioderPath}
							initialFill={initialUtbetalingsperiodeVerdier}
							initialErase={initialUtbetalingsperiode}
						/>
					</>
				)}
			</FormDollyFieldArray>
		</Vis>
	)
}
