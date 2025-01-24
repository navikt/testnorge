import styled from 'styled-components'
import React from 'react'
import { Button } from '@navikt/ds-react'
import { TrashIcon } from '@navikt/aksel-icons'
import { UseFormGetValues } from 'react-hook-form'
import { TestComponentSelectors } from '#/mocks/Selectors'

type HeaderProps = {
	title: string
	antall?: number
	paths: string[]
	getValues?: UseFormGetValues<any>
	emptyCategory?: Function
	dataCy?: string
}

export const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px solid #ccc;
	border-radius: 4px;
`

export const Soekefelt = styled.div`
	padding: 20px 15px 5px 15px;
`

export const SoekKategori = styled.div`
	display: flex;
	flex-wrap: wrap;
	font-size: medium;

	h4 {
		display: flex;
		align-items: center;
		margin: 15px 0 10px;
		width: 100%;
	}

	&& {
		.dolly-form-input {
			min-width: 0;
			flex-grow: 0;
		}
	}
`

export const Buttons = styled.div`
	margin: 15px 0 10px 0;

	&& {
		button {
			margin-right: 10px;
		}
	}
`

const KategoriHeader = styled.div`
	display: flex;
	align-items: center;
`

const KategoriCircle = styled.div`
	display: flex;
	width: 20px;
	height: 20px;
	border-radius: 50%;
	margin-left: 10px;
	background-color: #0067c5ff;

	&& {
		p {
			margin: auto;
			margin-top: -1px;
			font-size: 15px;
			font-weight: bold;
			color: white;
			padding-bottom: 5px;
		}
	}
`

const KategoriEmptyButtonWrapper = styled.div`
	position: absolute;
	right: 10px;
`

export const Header = ({ title, antall, paths, getValues, emptyCategory, dataCy }: HeaderProps) => {
	const antallValgt = antall ? antall : getAntallRequest(paths, getValues)
	return (
		<KategoriHeader data-testid={dataCy}>
			<span>{title}</span>
			{antallValgt > 0 && (
				<KategoriCircle data-testid={TestComponentSelectors.TITLE_TENOR_HEADER_COUNTER}>
					<p>{antallValgt}</p>
				</KategoriCircle>
			)}
			{paths && (
				<KategoriEmptyButtonWrapper>
					<Button
						onClick={(e) => {
							e.stopPropagation()
							emptyCategory?.(paths)
						}}
						data-testid={TestComponentSelectors.BUTTON_TENOR_CLEAR_HEADER}
						variant={'tertiary'}
						icon={<TrashIcon />}
						size={'small'}
						title="Tøm kategori"
					/>
				</KategoriEmptyButtonWrapper>
			)}
		</KategoriHeader>
	)
}

export const requestIsEmpty = (updatedRequest: any) => {
	let isEmpty = true
	const flatten = (obj: any) => {
		for (const i in obj) {
			if (typeof obj[i] === 'object' && !Array.isArray(obj[i])) {
				flatten(obj[i])
			} else {
				if (Array.isArray(obj[i])) {
					if (obj[i].length > 0) {
						isEmpty = false
					}
				} else if (obj[i] !== null && obj[i] !== false && obj[i] !== '') {
					isEmpty = false
				}
			}
		}
	}
	flatten(updatedRequest)
	return isEmpty
}

const getAntallRequest = (liste: string[], getValues: UseFormGetValues<any>) => {
	let antall = 0
	liste?.forEach((item) => {
		const attr = getValues?.(item)
		if (Array.isArray(attr)) {
			antall += attr.length
		} else if (attr || attr === false) {
			antall++
		}
	})
	return antall
}
