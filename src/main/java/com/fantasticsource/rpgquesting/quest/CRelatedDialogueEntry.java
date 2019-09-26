package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CRelatedDialogueEntry extends Component
{
    public CUUID dialogueID = new CUUID();
    public CStringUTF8 dialogueName = new CStringUTF8(), relation = new CStringUTF8();
    public CInt branchIndex = new CInt();

    public CRelatedDialogueEntry()
    {
    }

    public CRelatedDialogueEntry(CDialogueBranch branch, String relation)
    {
        dialogueID.set(branch.dialogue.permanentID.value);
        dialogueName.set(branch.dialogue.name.value);
        branchIndex.set(branch.dialogue.branches.indexOf(branch));
        this.relation.set(relation);
    }

    @Override
    public CRelatedDialogueEntry write(ByteBuf buf)
    {
        dialogueID.write(buf);
        dialogueName.write(buf);
        relation.write(buf);
        branchIndex.write(buf);

        return this;
    }

    @Override
    public CRelatedDialogueEntry read(ByteBuf buf)
    {
        dialogueID.read(buf);
        dialogueName.read(buf);
        relation.read(buf);
        branchIndex.read(buf);

        return this;
    }

    @Override
    public CRelatedDialogueEntry save(OutputStream stream)
    {
        dialogueID.save(stream);
        dialogueName.save(stream);
        relation.save(stream);
        branchIndex.save(stream);

        return this;
    }

    @Override
    public CRelatedDialogueEntry load(InputStream stream)
    {
        dialogueID.load(stream);
        dialogueName.load(stream);
        relation.load(stream);
        branchIndex.load(stream);

        return this;
    }
}
