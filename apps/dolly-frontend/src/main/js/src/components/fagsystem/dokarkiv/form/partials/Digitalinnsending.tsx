import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Kategori } from '@/components/ui/form/kategori/Kategori'

export default ({ path }: { path: string }) => {
	return (
		// @ts-ignore
		<div className="flexbox--full-width">
			<Kategori title={'Avsender'}>
				<div className="flexbox--flex-wrap">
					<FormSelect
						name={`${path}.avsenderMottaker.idType`}
						label={'Type'}
						options={Options('avsenderType')}
					/>
					<FormTextInput name={`${path}.avsenderMottaker.id`} label="ID" size={'large'} />
					<FormTextInput name={`${path}.avsenderMottaker.navn`} label="Navn" />
				</div>
			</Kategori>
		</div>
	)
}
