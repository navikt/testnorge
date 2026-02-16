import { useDispatch } from 'react-redux'
import { setSearchText } from '@/ducks/search'
import { FormProvider, useForm } from 'react-hook-form'
import { Search } from '@navikt/ds-react'
import styled from 'styled-components'

const StyledSearch = styled.div`
	width: 270px;
`

export const SearchField = ({
	placeholder = 'Hva leter du etter?',
	setText,
	size = 'medium',
	ref,
	...rest
}) => {
	const dispatch = useDispatch()
	const formMethods = useForm()

	const handleChange = (value) => {
		return setText ? setText(value?.trim()) : dispatch(setSearchText(value?.trim()))
	}

	return (
		<StyledSearch>
			<FormProvider {...formMethods}>
				<Search
					label={'Søk'}
					placeholder={placeholder}
					onChange={handleChange}
					aria-label="Search"
					size={size}
					maxLength={20}
					ref={ref}
					variant={'simple'}
					{...rest}
				/>
			</FormProvider>
		</StyledSearch>
	)
}
