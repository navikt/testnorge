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

function IdentList({ identer, closeModal }: { identer: string[]; closeModal?: () => void }) {
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
							dispatch(navigerTilPerson(cleanIdent)).then(() => {
								setLoading(false)
								closeModal?.()
							})
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

export default function WrappedIdentList({
	identer,
	closeModal,
}: {
	identer?: string[]
	closeModal?: () => void
}) {
	const [isOpen, toggleOpen] = useToggle(identer && identer.length <= 5)
	return (
		<div className="flexbox">
			{isOpen && <IdentList identer={identer} closeModal={closeModal} />}
			{!isOpen && <IdentSammendrag identer={identer} />}
			{identer && identer.length > 5 && <ExpandButton expanded={isOpen} onClick={toggleOpen} />}
		</div>
	)
}
