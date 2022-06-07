import React from 'react'
import { useToggle } from 'react-use'
import ExpandButton from '~/components/ui/button/ExpandButton'

function IdentList({ identer }: { identer: string[] }) {
    return (
        <ul>
            {identer.map((ident, idx) => (
                <li key={idx}>{ident}</li>
            ))}
        </ul>
    )
}

function IdentSammendrag({ identer }: { identer: string[] }) {
    return (
        <span>
            Totalt: {identer.length}
        </span>
    )
}

export default function WrappedIdentList({ identer }: { identer?: string[] }) {
    const [isOpen, toggleOpen] = useToggle(identer && identer.length <= 5)
    return (
        <div className="flexbox">
            {isOpen &&
                <IdentList identer={identer}/>
            }
            {!isOpen &&
                <IdentSammendrag identer={identer}/>
            }
            {identer && identer.length > 5 &&
                <ExpandButton expanded={isOpen} onClick={toggleOpen}/>
            }
        </div>
    )
}
