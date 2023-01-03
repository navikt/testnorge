import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialKjoenn } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

type KjoennTypes = {
	path: string
}

export const KjoennForm = ({ path }: KjoennTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.kjoenn`}
				label="Kjønn"
				options={Options('kjoenn')}
				size="large"
				isClearable={false}
			/>
			<AvansertForm path={path} kanVelgeMaster={true} />
		</>
	)
}

export const Kjoenn = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.kjoenn'}
				header="Kjønn"
				newEntry={initialKjoenn}
				canBeEmpty={false}
			>
				{(path: string) => <KjoennForm path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
