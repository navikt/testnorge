import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Kategori } from '@/components/ui/form/kategori/Kategori'

export default () => {
	return (
		// @ts-ignore
		<div className="flexbox--space">
			<Kategori title={'Avsender'}>
				<div className="flexbox--flex-wrap">
					<FormSelect
						name={'dokarkiv.avsenderMottaker.idType'}
						label={'Type'}
						options={Options('avsenderType')}
					/>
					<FormTextInput name="dokarkiv.avsenderMottaker.id" label="ID" size={'large'} />
					<FormTextInput name="dokarkiv.avsenderMottaker.navn" label="Navn" />
				</div>
			</Kategori>
		</div>
	)
}
