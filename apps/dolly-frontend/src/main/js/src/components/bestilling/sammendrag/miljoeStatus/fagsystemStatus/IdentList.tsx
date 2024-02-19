import { useToggle } from 'react-use'
import ExpandButton from '@/components/ui/button/ExpandButton/ExpandButton'
import { navigerTilPerson } from '@/ducks/finnPerson'
import { useDispatch } from 'react-redux'
import Button from '@/components/ui/button/Button'

function IdentList({ identer }: { identer: string[] }) {
	const dispatch = useDispatch()
	return (
		<ul>
			{identer.map((ident, idx) => (
				<Button
					style={{ alignContent: 'center' }}
					onClick={() => dispatch(navigerTilPerson(ident))}
					key={idx}
				>
					{ident}
				</Button>
			))}
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
