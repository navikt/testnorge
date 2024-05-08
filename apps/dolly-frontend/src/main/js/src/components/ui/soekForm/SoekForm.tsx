import styled from 'styled-components'
import React from 'react'
import { Button } from '@navikt/ds-react'
import { TrashIcon } from '@navikt/aksel-icons'

type HeaderProps = {
	title: string
	paths: Array<string>
	getValues: Function
	emptyCategory: Function
}

export const SoekefeltWrapper = styled.div`
	display: flex;
	flex-direction: column;
	margin-bottom: 20px;
	background-color: white;
	border: 1px @color-bg-grey-border;
	border-radius: 4px;
`

export const Soekefelt = styled.div`
	padding: 20px 15px 5px 15px;
`

export const SoekKategori = styled.div`
	display: flex;
	flex-wrap: wrap;
	font-size: medium;

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

const KategoriEmptyButton = styled(Button)`
	position: absolute;
	right: 10px;
`

export const Header = ({ title, paths, getValues, emptyCategory }: HeaderProps) => {
	const antall = getAntallRequest(paths, getValues)
	return (
		<KategoriHeader>
			<span>{title}</span>
			{antall > 0 && (
				<KategoriCircle>
					<p>{antall}</p>
				</KategoriCircle>
			)}
			<KategoriEmptyButton
				onClick={(e) => {
					e.stopPropagation()
					emptyCategory(paths)
				}}
				variant={'tertiary'}
				icon={<TrashIcon />}
				size={'small'}
				title="Tøm kategori"
			/>
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

const getAntallRequest = (liste: Array<string>, getValues) => {
	let antall = 0
	liste.forEach((item) => {
		const attr = getValues(item)
		if (Array.isArray(attr)) {
			antall += attr.length
		} else if (attr || attr === false) {
			antall++
		}
	})
	return antall
}
