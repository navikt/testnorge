import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

interface OppholdAnnetStedValues {
	path: string
}

export const OppholdAnnetSted = ({ path }: OppholdAnnetStedValues) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikSelect name={path} label="Opphold annet sted" options={Options('oppholdAnnetSted')} />
		</div>
	)
}
