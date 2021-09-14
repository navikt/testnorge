import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { Kategori } from '~/components/ui/form/kategori/Kategori'

export const Digitalinnsending = () => {
	return (
		// @ts-ignore
		<div className="flexbox--space">
			<Kategori title={'Avsender'}>
				<FormikSelect
					name={'dokarkiv.avsenderMottaker.idType'}
					label={'Type'}
					options={Options('avsenderType')}
				/>
				<FormikTextInput name="dokarkiv.avsenderMottaker.id" label="ID" size={'large'} />
				<FormikTextInput name="dokarkiv.avsenderMottaker.navn" label="Navn" />
			</Kategori>
		</div>
	)
}
