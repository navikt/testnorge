import { useToggle } from 'react-use'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import { navigerTilPerson } from '@/ducks/finnPerson'
import { useDispatch } from 'react-redux'
import Button from '@/components/ui/button/Button'
import { useState } from 'react'
import Loading from '@/components/ui/loading/Loading'
import styled from 'styled-components'

const NavigerTilIdentButton = styled(Button)`
	&&& {
		margin-left: unset;
		font-size: 15px;
	}

	span {
		margin-top: 0;
		padding-left: 0;
	}
`

function IdentList({ identer }: { identer: string[] }) {
	const dispatch = useDispatch()
	const [loading, setLoading] = useState(false)

	return (
		<ul>
			{identer.map((ident, idx) => {
				const cleanIdent = ident?.replace(/[()]/g, '')
				return loading ? (
					<Loading label={'Navigerer...'} key={idx} />
				) : (
					<NavigerTilIdentButton
						onClick={() => {
							setLoading(true)
							return dispatch(navigerTilPerson(cleanIdent))
						}}
						key={idx}
					>
						{ident}
					</NavigerTilIdentButton>
				)
			})}
		</ul>
	)
}

function IdentSammendrag({ identer }: { identer: string[] }) {
	return <span>Totalt: {identer.length}</span>
}

export default function WrappedIdentList({ identer }: { identer?: string[] }) {
	const [isOpen, toggleOpen] = useToggle(identer && identer.length <= 5)
	return (
		<div className="flexbox">
			{isOpen && <IdentList identer={identer} />}
			{!isOpen && <IdentSammendrag identer={identer} />}
			{identer && identer.length > 5 && <ExpandButton expanded={isOpen} onClick={toggleOpen} />}
		</div>
	)
}
