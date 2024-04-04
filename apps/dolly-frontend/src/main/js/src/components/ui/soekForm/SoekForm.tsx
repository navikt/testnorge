import styled from 'styled-components'
import React from 'react'

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

export const Header = ({ title, antall }) => (
	<KategoriHeader>
		<span>{title}</span>
		{antall > 0 && (
			<KategoriCircle>
				<p>{antall}</p>
			</KategoriCircle>
		)}
	</KategoriHeader>
)

export const requestIsEmpty = (updatedRequest) => {
	let isEmpty = true
	const flatten = (obj) => {
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
