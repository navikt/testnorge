import React from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { setSearchText } from '~/ducks/search'
import { TextInput } from '~/components/ui/form/inputs/textInput/TextInput'

import './SearchField.less'

export const SearchField = ({ placeholder = 'Hva leter du etter?' }) => {
	const searchText = useSelector(state => state.search)
	const dispatch = useDispatch()

	const handleChange = event => dispatch(setSearchText(event.target.value.trim()))

	return (
		<div className="searchfield-container skjemaelement">
			<TextInput
				value={searchText}
				id="searchfield-inputfield"
				type="text"
				placeholder={placeholder}
				onChange={handleChange}
				aria-label="Search"
				icon="search"
			/>
		</div>
	)
}
