import { useDispatch } from 'react-redux'
import { setSearchText } from '@/ducks/search'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'

import './SearchField.less'
import { FormProvider, useForm } from 'react-hook-form'

export const SearchField = ({ placeholder = 'Hva leter du etter?', setText, ...rest }) => {
	const dispatch = useDispatch()
	const formMethods = useForm()

	const handleChange = (event) => {
		return setText
			? setText(event.target.value.trim())
			: dispatch(setSearchText(event.target.value.trim()))
	}

	return (
		<div className="searchfield-container skjemaelement">
			<FormProvider {...formMethods}>
				<TextInput
					name="searchfield-inputfield"
					placeholder={placeholder}
					onChange={handleChange}
					aria-label="Search"
					icon="search"
					{...rest}
				/>
			</FormProvider>
		</div>
	)
}
