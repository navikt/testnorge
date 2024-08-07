import React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from '@/components/fagsystem/tjenestepensjon/form/validation'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useFormContext } from 'react-hook-form'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { UtbetalingsperioderForm } from '@/components/fagsystem/pensjonsavtale/form/partials/Utbetalingsperioder' // import './TPForm.less'
// import './TPForm.less'

export const avtalePath = 'pensjonforvalter.pensjonsavtale'
const hjelpetekst =
	'Pensjonsavtale inneholder beskrivelse av opptjeningsår, samt plangte perioder og grad for uttak.'

export const PensjonsavtaleForm = () => {
	const formMethods = useFormContext()

	return (
		<Vis attributt={avtalePath}>
			<Panel
				heading="Pensjonsavtale"
				hasErrors={panelError(avtalePath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [avtalePath])}
				informasjonstekst={hjelpetekst}
			>
				<div className="flexbox--flex-wrap">
					<FormTextInput
						name={`${avtalePath}.produktBetegnelse`}
						label="Produktbetegnelse"
						type="string"
					/>

					<FormSelect
						name={`${avtalePath}.avtaleKategori`}
						label="Avtalekategori"
						size={'medium'}
						options={Options('avtaleKategori')}
					/>

					<FormTextInput name={`${avtalePath}.startAlderAar`} label="Startalder År" type="number" />

					<FormTextInput name={`${avtalePath}.sluttAlderAar`} label="Sluttalder År" type="number" />

					<UtbetalingsperioderForm formMethods={formMethods} />
				</div>
			</Panel>
		</Vis>
	)
}

PensjonsavtaleForm.validation = validation
