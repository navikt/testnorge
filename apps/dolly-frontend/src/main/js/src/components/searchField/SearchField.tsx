import { useDispatch } from 'react-redux'
import { setSearchText } from '@/ducks/search'

import './SearchField.less'
import { FormProvider, useForm } from 'react-hook-form'
import { Search } from '@navikt/ds-react'

export const SearchField = ({
	placeholder = 'Hva leter du etter?',
	setText,
	label = 'Søk',
	shortcutKey,
	ref,
	...rest
}) => {
	const dispatch = useDispatch()
	const formMethods = useForm()

	const handleChange = (value) => {
		return setText ? setText(value?.trim()) : dispatch(setSearchText(value?.trim()))
	}

	const focusSearchInput = (event: MouseEvent) => {
		event.preventDefault()
		const inputElement = ref.current
		if (inputElement) {
			inputElement.focus()
		}
	}

	return (
		<div className="searchfield-container skjemaelement">
			<FormProvider {...formMethods}>
				<Search
					label={label}
					placeholder={placeholder}
					onChange={handleChange}
					aria-label="Search"
					size={'small'}
					maxLength={20}
					ref={ref}
					variant={'secondary'}
					{...rest}
				>
					<Search.Button onClick={focusSearchInput}>{shortcutKey}</Search.Button>
				</Search>
			</FormProvider>
		</div>
	)
}
