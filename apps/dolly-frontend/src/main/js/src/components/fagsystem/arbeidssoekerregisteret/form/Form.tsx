import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { useFormContext } from 'react-hook-form'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'

export const arbeidssoekerregisteretAttributt = 'arbeidssoekerregisteret'

export const ArbeidssoekerregisteretForm = () => {
	const formMethods = useFormContext()
	return (
		<Vis attributt={arbeidssoekerregisteretAttributt}>
			<Panel
				heading="Arbeidssoekerregisteret"
				hasErrors={panelError(arbeidssoekerregisteretAttributt)}
				iconType="cv"
				startOpen={erForsteEllerTest(formMethods.getValues(), [arbeidssoekerregisteretAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<FormSelect name="arbeidssoekerregisteret.utfoertAv" label="Utført av" options={[]} />
				</div>
			</Panel>
		</Vis>
	)
}

//TODO: Kodeverk Yrkesklassifisering brukes til stillingStyrk08 og stillingstittel
