import { useFormContext } from 'react-hook-form'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { etterlatteYtelserAttributt } from '@/components/bestillingsveileder/stegVelger/steg/steg1/paneler/EtterlatteYtelser'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'

export const initialEtterlatteYtelser = () => ({
	ytelse: '',
	soeker: '',
})

export const EtterlatteYtelserForm = () => {
	const formMethods = useFormContext()

	return (
		<Vis attributt={etterlatteYtelserAttributt}>
			<Panel
				heading="Etterlatteytelser"
				hasErrors={panelError(etterlatteYtelserAttributt)}
				iconType="grav"
				startOpen={erForsteEllerTest(formMethods.getValues(), [etterlatteYtelserAttributt])}
			>
				<ErrorBoundary>
					<FormDollyFieldArray
						name="etterlatteYtelser"
						header="Etterlatteytelse"
						newEntry={initialEtterlatteYtelser}
						canBeEmpty={false}
					>
						{(path: string) => (
							<>
								<FormSelect
									name={`${path}.ytelse`}
									label="Ytelse"
									options={Options('etterlatteYtelser')}
									size="large"
									isClearable={false}
								/>
							</>
						)}
					</FormDollyFieldArray>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}
