import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

const personstatusPath = 'personstatus.status'

export const Personstatus = () => (
	<section>
		<FormSelect
			name={personstatusPath}
			label="Folkeregisterpersonstatus"
			options={Options('personstatus')}
			size="medium"
		/>
	</section>
)

export const PersonstatusPaths = [personstatusPath]
